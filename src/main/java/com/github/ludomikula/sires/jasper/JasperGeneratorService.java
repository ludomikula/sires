package com.github.ludomikula.sires.jasper;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ludomikula.sires.ProcessingException;
import com.github.ludomikula.sires.data.ExporterSettings;
import com.github.ludomikula.sires.data.GeneratorRequest;
import com.github.ludomikula.sires.generator.GeneratorService;
import com.github.ludomikula.sires.jasper.exporter.PdfExporterSettings;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JasperGeneratorService implements GeneratorService {

    public static final String TEMPLATE_DATA_PROPERTY = "JSON_INPUT_DATA";

    @Value("${generator.templates.folder:./sires/templates}")
    private String templateFodler;

    private final Map<String, JasperReport> templateCache = new ConcurrentHashMap<>();
    private final Map<String, String> templateHashCache = new ConcurrentHashMap<>();

    @Override
    public byte[] generateDocument(GeneratorRequest request) {
        Map<String, Object> params = new HashMap<>(request.getParameters());
        JasperPrint jasperPrint = populateTemplate(request.getTemplate(), params, request.getData());
        JRAbstractExporter exporter = createJRExporter(request.getDocumentType(), request.getSettings());
        return exportDocument(exporter, jasperPrint);
    }

    private JasperPrint populateTemplate(String templateId, Map<String, Object> parameters, JsonNode data) {
        JasperReport report = getTemplate(templateId);
        JasperPrint jasperPrint;
        try {
            parameters.put(TEMPLATE_DATA_PROPERTY, data.toString());
            jasperPrint = JasperFillManager.fillReport(report, parameters, new JsonDataSource(new ByteArrayInputStream(data.toString().getBytes(StandardCharsets.UTF_8))));
        }
        catch (Exception cause) {
            throw new ProcessingException("Failed to populate template " + templateId + "!", cause);
        }

        return jasperPrint;
    }

    private JasperReport loadTemplate(Path templatePath) {
        try {
            return (JasperReport)JRLoader.loadObject(templatePath.toFile());
        }
        catch (Exception cause) {
            throw new ProcessingException("Failed to load jasper template " + templatePath.getFileName() + "!", cause);
        }
    }

    private JasperReport getTemplate(String templateId) {
        JasperReport report = null;
        Path jreportPath = Paths.get(templateFodler, templateId + ".jasper");

        String templateHash;
        try {
            templateHash = DigestUtils.md5Hex(Files.readAllBytes(jreportPath));
        }
        catch (Exception cause) {
            throw new ProcessingException("Failed to read template " + jreportPath + "!", cause);
        }

        if (templateHashCache.containsKey(templateId) && templateHashCache.get(templateId).equals(templateHash) && templateCache.containsKey(templateId)) {
            report = templateCache.get(templateId);
        }
        else {
            report = loadTemplate(jreportPath);
            templateHashCache.put(templateId, templateHash);
            templateCache.put(templateId, report);
        }
        return report;
    }

    private JRAbstractExporter createJRExporter(GeneratorRequest.DocumentType documentType, ExporterSettings settings) {
        JRAbstractExporter exporter;

        switch (documentType) {
            case PDF:
                exporter = new JRPdfExporter();
                PdfExporterSettings pdfExporterSettings = (settings == null ? PdfExporterSettings.defaultSettings() : (PdfExporterSettings) settings);
                exporter.setConfiguration(PdfExporterSettings.toPdfExporterConfiguration(pdfExporterSettings));
                break;
            default:
                throw new ProcessingException("Unknown document type: " + documentType+ "!", null);
        }

        return exporter;
    }

    private byte[] exportDocument(JRAbstractExporter exporter, JasperPrint jasperPrint) {
        try {
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            ByteArrayOutputStream documentStream = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(documentStream));
            exporter.exportReport();
            documentStream.close();
            return documentStream.toByteArray();
        }
        catch (Exception cause) {
            throw new ProcessingException("Failed to export Jasper Report document!", cause);
        }
    }
}
