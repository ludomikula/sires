package com.github.ludomikula.sires.jasper.exporter;

import com.github.ludomikula.sires.data.ExporterSettings;
import lombok.*;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PdfExporterSettings implements ExporterSettings {

    private Boolean compressed;
    private Boolean encrypted;
    private PdfVersionEnum pdfVersion;
    private String userPassword;
    private String ownerPassword;
    private Boolean tagged;

    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;


    public static SimplePdfExporterConfiguration toPdfExporterConfiguration(PdfExporterSettings settings) {
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

        if (settings.getCompressed() != null) {
            configuration.setCompressed(settings.getCompressed());
        }
        if (settings.getEncrypted() != null) {
            configuration.setEncrypted(settings.getEncrypted());
        }
        if (settings.getPdfVersion() != null) {
            configuration.setPdfVersion(settings.getPdfVersion());
        }
        if (settings.getUserPassword() != null) {
            configuration.setUserPassword(settings.getUserPassword());
        }
        if (settings.getOwnerPassword() != null) {
            configuration.setOwnerPassword(settings.getOwnerPassword());
        }
        if (settings.getTagged() != null) {
            configuration.setTagged(settings.getTagged());
        }
        if (settings.getTitle() != null) {
            configuration.setMetadataTitle(settings.getTitle());
        }
        if (settings.getAuthor() != null) {
            configuration.setMetadataAuthor(settings.getAuthor());
        }
        if (settings.getSubject() != null) {
            configuration.setMetadataSubject(settings.getSubject());
        }
        if (settings.getKeywords() != null) {
            configuration.setMetadataKeywords(settings.getKeywords());
        }
        if (settings.getCreator() != null) {
            configuration.setMetadataCreator(settings.getCreator());
        }
        if (settings.getProducer() != null) {
            configuration.setMetadataProducer(settings.getProducer());
        }

        return configuration;
    }

    public static PdfExporterSettings defaultSettings() {
        return PdfExporterSettings.builder()
                .compressed(false)
                .encrypted(false)
                .pdfVersion(PdfVersionEnum.VERSION_1_5)
                .userPassword(null)
                .ownerPassword(null)
                .tagged(false)
                .title(null)
                .author(null)
                .subject(null)
                .keywords(null)
                .creator(null)
                .producer(null)
                .build();
    }
}
