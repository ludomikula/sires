package com.github.ludomikula.sires.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.ludomikula.sires.data.GeneratorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileBasedReportCacheService implements ReportCacheService {
    private final ObjectMapper objectMapper;

    @Value("${generator.reports.keepFor:300}")
    private long cacheTimeout;

    @Value("${generator.reports.cache:./sires/reports}")
    private String cacheFolder;

    @Override
    public boolean cacheReport(GeneratorRequest request, byte[] reportData) {
        Path cacheFilePath = computeReportFileName(request);
        if (cacheFilePath == null) {
            return false;
        }

        try {
            Files.write(cacheFilePath, reportData, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            log.info("Report cached as: {}", cacheFilePath);
            return true;
        }
        catch (Exception cause) {
            log.error("Error caching report!", cause);
            return false;
        }
    }

    @Override
    public boolean isReportCached(GeneratorRequest request) {
        Path cacheFileName = computeReportFileName(request);
        if (cacheFileName == null || Files.notExists(cacheFileName)) {
            return false;
        }

        return !isCacheFileExpired(cacheFileName);
    }

    @Override
    public byte[] getReport(GeneratorRequest request) {
        Path cacheFileName = computeReportFileName(request);

        try {
            if (cacheFileName != null && cacheFileName.toFile().exists()) {
                log.info("Reading cached report from {}", cacheFileName);
                return Files.readAllBytes(cacheFileName);
            }
        }
        catch (Exception cause) {
            log.error("Error reading cached report!", cause);
        }
        return null;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @Override
    public void clearCache() {
        String[] extensions = Arrays.stream(GeneratorRequest.DocumentType.values())
                .map(GeneratorRequest.DocumentType::extension)
                .toArray(String[]::new);
        try (Stream<Path> paths = Files.walk(Paths.get(cacheFolder))) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isCacheFileExpired)
                    .filter(path -> path.getFileName().toString().length() > 32)    // cached file name is an MD5 hash, so must be at least 32 characters long + extension
                    .filter(path -> StringUtils.endsWithAny(path.getFileName().toString(), extensions))
                    .forEach(path -> {
                        try {
                            if (path.toFile().delete()) {
                                log.info("Deleted cached report file: {}", path);
                            }
                            else {
                                log.warn("Failed to delete cached report file: {}", path);
                            }
                        }
                        catch (Exception cause) {
                            log.error("Error removing cached report: {} !", path, cause);
                        }
                    });
        }
        catch (Exception cause) {
            log.error("Error clearing cached reports!", cause);
        }
    }

    private Path computeReportFileName(GeneratorRequest request) {
        StringBuilder data = new StringBuilder();
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            data.append("document type: ").append(request.getDocumentType().name()).append("\n")
                .append("template: ").append(request.getTemplate()).append("\n")
                .append("settings: ").append(writer.writeValueAsString(request.getSettings())).append("\n")
                .append("parameters: ").append(writer.writeValueAsString(request.getParameters())).append("\n")
                .append("data: ").append(writer.writeValueAsString(request.getData().toPrettyString()));
            return Path.of(cacheFolder, DigestUtils.md5Hex(data.toString()) + request.getDocumentType().extension());
        }
        catch (Exception cause) {
            log.error("Failed to compute report cache file name!", cause);
            return null;
        }
    }

    private boolean isCacheFileExpired(Path path) {
        try {
            long age = (System.currentTimeMillis() - Files.getLastModifiedTime(path).toMillis()) / 1000;
            if (age > cacheTimeout) {
                return true;
            }
        }
        catch (Exception cause) {
            return false;
        }
        return false;
    }
}
