package com.tp.tradexcelsior.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.tradexcelsior.dto.request.BookRequestDto;
import com.tp.tradexcelsior.dto.request.ReferenceRequestDto;
import com.tp.tradexcelsior.dto.response.ReferenceResponseDto;
import com.tp.tradexcelsior.service.impl.ImageService;
import com.tp.tradexcelsior.service.impl.ReferenceService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Reference Management", description = "APIs for managing references")
@RestController
@RequestMapping("/api/v1/reference")
public class ReferenceController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private Validator validator;

    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    // Get all references
    @Operation(summary = "Get all references")
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ReferenceResponseDto>>> getAllReferences() {
        ResponseWrapper<List<ReferenceResponseDto>> allReferences = referenceService.getAllReferences();
        return ResponseEntity.ok(allReferences);
    }

    // Get a single reference by ID
    @Operation(summary = "Get a reference by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ReferenceResponseDto>> getReferenceById(@PathVariable String id) {
        ResponseWrapper<ReferenceResponseDto> referenceById = referenceService.getReferenceById(id);
        return ResponseEntity.ok(referenceById);
    }

    @Operation(summary = "Add a new reference")
    @PostMapping
    public ResponseEntity<ResponseWrapper<ReferenceResponseDto>> createReference(@RequestPart("reference") String referenceDTOString, @RequestPart(value = "image", required = false) MultipartFile image) {

        ReferenceRequestDto referenceRequestDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            referenceRequestDto = objectMapper.readValue(referenceDTOString, ReferenceRequestDto.class);
        } catch (IOException e) {
            // Handle JSON parsing error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
        }

        // Validate the DTO manually
        Set<ConstraintViolation<ReferenceRequestDto>> violations = validator.validate(
            referenceRequestDto);
        if (!violations.isEmpty()) {
            // Collect validation errors
            Map<String, String> errorMessages = new HashMap<>();
            for (ConstraintViolation<ReferenceRequestDto> violation : violations) {
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
        }

        ResponseWrapper<ReferenceResponseDto> reference = referenceService.createReference(
            referenceRequestDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(reference);
    }

    // Update an existing reference
    @Operation(summary = "Update a reference")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ReferenceResponseDto>> updateReference(@PathVariable String id, @RequestPart("reference") String referenceRequestDtoString, @RequestPart(value = "image", required = false) MultipartFile image) {

        ReferenceRequestDto referenceRequestDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            referenceRequestDto = objectMapper.readValue(referenceRequestDtoString, ReferenceRequestDto.class);
        } catch (IOException e) {
            // Handle JSON parsing error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
        }

        // Validate the DTO manually
        Set<ConstraintViolation<ReferenceRequestDto>> violations = validator.validate(referenceRequestDto);
        if (!violations.isEmpty()) {
            // Collect validation errors
            Map<String, String> errorMessages = new HashMap<>();
            for (ConstraintViolation<ReferenceRequestDto> violation : violations) {
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
        }

        ResponseWrapper<ReferenceResponseDto> response= referenceService.updateReference(
            id, referenceRequestDto, image);
        return ResponseEntity.ok(response);
    }

    // Delete a reference by ID
    @Operation(summary = "Delete a reference")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteReference(@PathVariable String id) {
        ResponseWrapper<String> response = referenceService.deleteReference(id);
        return ResponseEntity.ok(response);
    }
}
