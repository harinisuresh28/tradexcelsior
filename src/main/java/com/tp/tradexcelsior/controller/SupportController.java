package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.request.SupportRequestDto;
import com.tp.tradexcelsior.dto.request.SupportUpdateDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SupportResponseDto;
import com.tp.tradexcelsior.service.impl.SupportService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Support Management", description = "APIs for managing support queries")
@RestController
@RequestMapping("/api/v1/support")
public class SupportController {

  @Autowired
  private SupportService supportService;

  @Operation(summary = "Create a new support entry", description = "Create a new support request and add it to the system.")
  @PostMapping
  public ResponseEntity<ResponseWrapper<SupportResponseDto>> addSupport(@RequestBody @Valid SupportRequestDto supportRequestDto){
    ResponseWrapper<SupportResponseDto> supportResponseDto = supportService.addSupport(supportRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(supportResponseDto);
  }

  @Operation(summary = "Get support entry by ID", description = "Fetch the details of a support entry by the given support ID.")
  @GetMapping("/{supportId}")
  public ResponseEntity<ResponseWrapper<SupportResponseDto>> getSupport(@PathVariable String supportId){
    ResponseWrapper<SupportResponseDto> supportResponseDto = supportService.getSupport(supportId);
    return ResponseEntity.ok(supportResponseDto);
  }

  @Operation(summary = "Get all support entries", description = "Fetch all support entries with pagination.")
  @GetMapping
  public ResponseEntity<ResponseWrapper<PagedResponse<SupportResponseDto>>> getAllSupport(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    ResponseWrapper<PagedResponse<SupportResponseDto>> pagedResponse = supportService.getAllSupport(page, size);
    return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
  }

  @Operation(summary = "Get all resolved support entries", description = "Fetch all resolved support entries with pagination.")
  @GetMapping("/resolved")
  public ResponseEntity<ResponseWrapper<PagedResponse<SupportResponseDto>>> getAllResolvedSupport(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    ResponseWrapper<PagedResponse<SupportResponseDto>> pagedResponse = supportService.getResolvedSupport(page, size);
    return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
  }

  @Operation(summary = "Get all unresolved support entries", description = "Fetch all unresolved support entries with pagination.")
  @GetMapping("/unresolved")
  public ResponseEntity<ResponseWrapper<PagedResponse<SupportResponseDto>>> getAllUnresolvedSupport(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    ResponseWrapper<PagedResponse<SupportResponseDto>> pagedResponse = supportService.getUnresolvedSupport(page, size);
    return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
  }

  @Operation(summary = "Update an existing support entry", description = "Update the details of a support entry using the provided update data.")
  @PutMapping
  public ResponseEntity<ResponseWrapper<SupportResponseDto>> updateSupport(@RequestBody @Valid SupportUpdateDto updateDto) {

    ResponseWrapper<SupportResponseDto> updatedSupport = supportService.updateSupport(updateDto);
    return new ResponseEntity<>(updatedSupport, HttpStatus.OK);
  }

  @Operation(summary = "Delete a Support Entry", description = "Delete a support entry by the given supportId.")
  @DeleteMapping("/{supportId}")
  public ResponseEntity<ResponseWrapper<String>> deleteSupport(@PathVariable String supportId) {
    ResponseWrapper<String> response = supportService.deleteSupport(supportId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
