package com.github.ludomikula.sires.generator;

import com.github.ludomikula.sires.cache.ReportCacheService;
import com.github.ludomikula.sires.data.GeneratorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GeneratorController implements GeneratorEndpoints {

    private final GeneratorService generatorService;
    private final ReportCacheService reportCacheService;

    @Override
    public ResponseEntity<byte[]> createDocument(GeneratorRequest data) {
        byte[] document = null;
        if (reportCacheService.isReportCached(data)) {
            document = reportCacheService.getReport(data);
            log.info("Returning cached report for template: '{}', format: '{}'", data.getTemplate(), data.getDocumentType().name());
        }
        if (document == null) {
            document = generatorService.generateDocument(data);
            if (document != null) {
                reportCacheService.cacheReport(data, document);
            }
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + data.getReportFileName() + "\"")
                .contentType(data.getDocumentType().contentType())
                .body(document);
    }
}
