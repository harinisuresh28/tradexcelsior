package com.tp.tradexcelsior.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.tradexcelsior.dto.request.SuccessStoriesRequestDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SuccessStoriesResponseDto;
import com.tp.tradexcelsior.service.impl.ImageService;
import com.tp.tradexcelsior.service.impl.SuccessStoriesService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Success Stories Management", description = "APIs for managing success stories")
@RestController
@RequestMapping("/api/v1/success-stories")
public class SuccessStoriesController {

  @Autowired
  private SuccessStoriesService successStoriesService;

  @Autowired
  private ImageService imageService;

  @Autowired
  private Validator validator;

  @Operation(summary = "Add a new Success Story", description = "This endpoint allows you to add a new success story.")
  @PostMapping
  public ResponseEntity<ResponseWrapper<SuccessStoriesResponseDto>> addSuccessStories(
      @RequestPart("successStory") String successStoriesRequestDtoString,
      @RequestPart(value = "image", required = false) MultipartFile image) {

    // Manually deserialize the JSON string into SuccessStoriesRequestDto
    SuccessStoriesRequestDto successStoriesRequestDto;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      successStoriesRequestDto = objectMapper.readValue(successStoriesRequestDtoString, SuccessStoriesRequestDto.class);
    } catch (IOException e) {
      // Handle JSON parsing error
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
    }

    // Validate the DTO manually
    Set<ConstraintViolation<SuccessStoriesRequestDto>> violations = validator.validate(successStoriesRequestDto);
    if (!violations.isEmpty()) {
      // Collect validation errors
      Map<String, String> errorMessages = new HashMap<>();
      for (ConstraintViolation<SuccessStoriesRequestDto> violation : violations) {
        errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
    }

    // If valid, proceed with the service logic
    ResponseWrapper<SuccessStoriesResponseDto> savedSuccessStoriesResponseDto =
        successStoriesService.addSuccessStoriesWithImage(successStoriesRequestDto, image);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedSuccessStoriesResponseDto);
  }


  @Operation(summary = "Get a Success Story by userName", description = "Fetch a success story by the given userName.")
  @GetMapping("/{userName}")
  public ResponseEntity<ResponseWrapper<SuccessStoriesResponseDto>> getSuccessStoryByUserName(@PathVariable String userName) {
    ResponseWrapper<SuccessStoriesResponseDto> successStoriesResponseDto = successStoriesService.getSuccessStoryByUserName(userName);
    return ResponseEntity.ok(successStoriesResponseDto);
  }

  @Operation(summary = "Get all Success Stories with pagination", description = "Retrieve all success stories with pagination support.")
  @GetMapping
  public ResponseEntity<ResponseWrapper<PagedResponse<SuccessStoriesResponseDto>>> getAllSuccessStories(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    ResponseWrapper<PagedResponse<SuccessStoriesResponseDto>> pagedResponse = successStoriesService.getAllSuccessStories(page, size);
    return ResponseEntity.ok(pagedResponse);
  }


  @Operation(summary = "Update a Success Story", description = "Update an existing success story with new data.")
  @PutMapping("/{userName}")
  public ResponseEntity<ResponseWrapper<SuccessStoriesResponseDto>> updateSuccessStory(
      @PathVariable String userName,
      @RequestPart("successStory") String successStoriesRequestDtoString,
      @RequestPart(value = "image", required = false) MultipartFile image) {

    // Manually deserialize the JSON string into SuccessStoriesRequestDto
    SuccessStoriesRequestDto successStoriesRequestDto;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      successStoriesRequestDto = objectMapper.readValue(successStoriesRequestDtoString, SuccessStoriesRequestDto.class);
    } catch (IOException e) {
      // Handle JSON parsing error
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
    }

    // Validate the DTO manually
    Set<ConstraintViolation<SuccessStoriesRequestDto>> violations = validator.validate(successStoriesRequestDto);
    if (!violations.isEmpty()) {
      // Collect validation errors
      Map<String, String> errorMessages = new HashMap<>();
      for (ConstraintViolation<SuccessStoriesRequestDto> violation : violations) {
        errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
    }

    // If valid, proceed with the service logic
    ResponseWrapper<SuccessStoriesResponseDto> updatedSuccessStoryDto =
        successStoriesService.updateSuccessStory(userName,successStoriesRequestDto, image);

    return ResponseEntity.status(HttpStatus.OK).body(updatedSuccessStoryDto);
  }

  @Operation(summary = "Delete a Success Story", description = "Delete a success story by the given userName.")
  @DeleteMapping("/{userName}")
  public ResponseEntity<ResponseWrapper<String>> deleteSuccessStory(@PathVariable String userName) {
    ResponseWrapper<String> response = successStoriesService.deleteSuccessStory(userName);
    return ResponseEntity.ok(response);
  }

}