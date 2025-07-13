package com.github.ludomikula.sires.cache;

import com.github.ludomikula.sires.data.GeneratorRequest;

public interface ReportCacheService {
    boolean isReportCached(GeneratorRequest request);
    byte[] getReport(GeneratorRequest request);
    boolean cacheReport(GeneratorRequest request, byte[] reportData);
    void clearCache();
}
