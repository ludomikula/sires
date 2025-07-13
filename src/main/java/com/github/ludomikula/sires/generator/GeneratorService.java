package com.github.ludomikula.sires.generator;

import com.github.ludomikula.sires.data.GeneratorRequest;

public interface GeneratorService {
    byte[] generateDocument(GeneratorRequest request);
}
