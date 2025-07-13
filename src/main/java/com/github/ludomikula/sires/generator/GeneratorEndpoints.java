package com.github.ludomikula.sires.generator;

import com.github.ludomikula.sires.data.GeneratorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/generate")
public interface GeneratorEndpoints {

    @Operation(
            tags = { "Document generator" },
            operationId = "createDocument",
            summary = "Generate document from posted data.",
            description = "Generate document from posted data."
    )
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.ALL_VALUE })
    ResponseEntity<byte[]> createDocument(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data to be filled into the template", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = { @ExampleObject(value = "{\r\n}")}))
            @RequestBody @Valid GeneratorRequest data);
}
