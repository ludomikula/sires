package com.github.ludomikula.sires.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Schema(description = "Generator request defining settings, properties and data pushed to the template.")
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

    @Schema(description = "Defines what template to use to generate the report. Use template filename without extension. (For example if template file is 'mytemplate.jasper', template should be 'mytemplate')")
    @NotBlank
    private String template;

    @Schema(description = "Determines file type of generated report.", defaultValue = "PDF")
    @NotNull
    private DocumentType documentType = DocumentType.PDF;

    @Schema(description = "Filename of generated file. If not specified, it's created from 'report' + timestamp + extension of document type, for example: 'report-1752416118.pdf'")
    private String documentName;

    @Schema(description = "Settings specific for generated file type.")
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "documentType"
    )
    @JsonSubTypes(
            @JsonSubTypes.Type(value = PdfExporterSettings.class, name = "PDF")
    )
    private ExporterSettings settings;

    @Schema(description = "Parameters injected into template. Within template, they can be access using $P{property_name}.")
    private Map<String, Object> parameters = new HashMap<>();

    @Schema(description = "Data injected into template as a JsonDataSource and also as JSON_INPUT_DATA parameter.")
    private JsonNode data;

    @JsonIgnore
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
