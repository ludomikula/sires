package com.github.ludomikula.sires.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ludomikula.sires.jasper.exporter.PdfExporterSettings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Schema
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeneratorRequest {

    public enum DocumentType {
        PDF(".pdf", MediaType.APPLICATION_PDF);

        private final String extension;
        private final MediaType contentType;

        DocumentType(String extension, MediaType contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        public String extension() {
            return this.extension;
        }

        public MediaType contentType() {
            return this.contentType;
        }
    }

    @NotBlank
    private String template;
    @NotNull
    private DocumentType documentType = DocumentType.PDF;
    private String documentName;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "documentType"
    )
    @JsonSubTypes(
            @JsonSubTypes.Type(value = PdfExporterSettings.class, name = "PDF")
    )
    private ExporterSettings settings;
    private Map<String, Object> parameters = new HashMap<>();
    private JsonNode data;

    public String getReportFileName() {
        if (StringUtils.isBlank(documentName)) {
            return "report-" + System.currentTimeMillis() + documentType.extension();
        } else {
            if (StringUtils.endsWithIgnoreCase(documentName, documentType.extension())) {
                return documentName;
            } else {
                return documentName + documentType.extension();
            }
        }
    }
}
