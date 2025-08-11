package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.dto.request.SupportRequestDto;
import com.tp.tradexcelsior.dto.request.SupportUpdateDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SupportResponseDto;
import com.tp.tradexcelsior.entity.Support;
import com.tp.tradexcelsior.exception.custom.SupportNotFoundException;
import com.tp.tradexcelsior.repo.SupportRepo;
import com.tp.tradexcelsior.service.ISupportService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SupportService implements ISupportService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private SupportRepo supportRepo;

  @Override
  @Transactional
  public ResponseWrapper<SupportResponseDto> addSupport(SupportRequestDto supportRequestDto) {

    // Map the DTO to the Support entity
    Support support = modelMapper.map(supportRequestDto, Support.class);

    try {
      // Save the support
      Support savedSupport = supportRepo.save(support);

      // Log the successful save and return the response DTO
      log.info("New support query added successfully : {}", savedSupport);
      SupportResponseDto responseDto = modelMapper.map(savedSupport, SupportResponseDto.class);
      return ResponseWrapper.success(HttpStatus.CREATED.value(), responseDto, "New support query added successfully.");
    } catch (Exception ex) {
      throw new RuntimeException("An unexpected error occurred: Support query not added", ex);
    }
  }

  @Override
  public ResponseWrapper<SupportResponseDto> getSupport(String supportId) {
    Support support = supportRepo.findByIdAndIsDeletedFalse(supportId)
        .orElseThrow(() -> {
          log.error("Support entry not found for ID: {}", supportId);
          return new SupportNotFoundException("Support entry not found for ID: " + supportId);
        });

    log.info("Fetching support entry with ID: {}", supportId);
    SupportResponseDto supportResponseDto = modelMapper.map(support, SupportResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), supportResponseDto, "Support query with id "+ supportId + " fetched successfully");
  }


  @Override
  public ResponseWrapper<PagedResponse<SupportResponseDto>> getAllSupport(int page, int size) {
    // Count the total number of support queries
    long totalItems = supportRepo.countByIsDeletedFalse();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // Handle the case where the requested page exceeds available pages
    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(List.of(), (int) totalItems,
          totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
    }

    // Default size handling
    if (size < 1) {
      size = 10;
    } else if (size > 100) {
      size = 100;
    }

    // Create a Pageable object with the given page and size
    Pageable pageable = PageRequest.of(page, size);

    // Fetch the paginated data
    Page<Support> supportPage = supportRepo.findByIsDeletedFalse(pageable);

    // Map the entities to DTOs
    List<SupportResponseDto> supportResponseDtoList = supportPage.getContent().stream()
        .map(support -> modelMapper.map(support, SupportResponseDto.class))
        .toList();

    // Log the info
    log.info("Fetched {} support query, page {} of {}.", supportResponseDtoList.size(), page, totalPages);

    // Return the paginated response
    PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(supportResponseDtoList, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
  }

  @Override
  public ResponseWrapper<PagedResponse<SupportResponseDto>> getResolvedSupport(int page, int size) {
    // Count the total number of support queries
    long totalItems = supportRepo.countByResolvedTrueAndIsDeletedFalse();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // Handle the case where the requested page exceeds available pages
    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(List.of(), (int) totalItems,
          totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
    }

    // Default size handling
    if (size < 1) {
      size = 10;
    } else if (size > 100) {
      size = 100;
    }

    // Create a Pageable object with the given page and size
    Pageable pageable = PageRequest.of(page, size);

    // Fetch the paginated data
    Page<Support> supportPage = supportRepo.findByResolvedTrueAndIsDeletedFalse(pageable);

    // Map the entities to DTOs
    List<SupportResponseDto> supportResponseDtoList = supportPage.getContent().stream()
        .map(support -> modelMapper.map(support, SupportResponseDto.class))
        .toList();

    // Log the info
    log.info("Fetched {} support query, page {} of {}.", supportResponseDtoList.size(), page, totalPages);

    // Return the paginated response
    PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(supportResponseDtoList, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
  }

  @Override
  public ResponseWrapper<PagedResponse<SupportResponseDto>> getUnresolvedSupport(int page, int size) {
    // Count the total number of support queries
    long totalItems = supportRepo.countByResolvedFalseAndIsDeletedFalse();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // Handle the case where the requested page exceeds available pages
    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(List.of(), (int) totalItems,
          totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
    }

    // Default size handling
    if (size < 1) {
      size = 10;
    } else if (size > 100) {
      size = 100;
    }

    // Create a Pageable object with the given page and size
    Pageable pageable = PageRequest.of(page, size);

    // Fetch the paginated data
    Page<Support> supportPage = supportRepo.findByResolvedFalseAndIsDeletedFalse(pageable);

    // Map the entities to DTOs
    List<SupportResponseDto> supportResponseDtoList = supportPage.getContent().stream()
        .map(support -> modelMapper.map(support, SupportResponseDto.class))
        .toList();

    // Log the info
    log.info("Fetched {} support query, page {} of {}.", supportResponseDtoList.size(), page, totalPages);

    // Return the paginated response
    PagedResponse<SupportResponseDto> pagedResponse = new PagedResponse<>(supportResponseDtoList, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of support queries.");
  }

  @Override
  @Transactional
  public ResponseWrapper<SupportResponseDto> updateSupport(SupportUpdateDto updateDto) {
    Support support = supportRepo.findByIdAndIsDeletedFalse(updateDto.getSupportId())
        .orElseThrow(() -> {
          log.error("Support entry not found for ID: {}", updateDto.getSupportId());
          return new SupportNotFoundException("Support entry not found for ID: " + updateDto.getSupportId());
        });

    if(updateDto.isResolved()){
      support.setResolved(true);
      support.setLastModified(LocalDateTime.now());
      Support updatedSupport = supportRepo.save(support);
      SupportResponseDto responseDto = modelMapper.map(updatedSupport, SupportResponseDto.class);
      return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Query set to resolved");
    }else {
      support.setResolved(false);
      support.setLastModified(LocalDateTime.now());
      Support updatedSupport = supportRepo.save(support);
      SupportResponseDto responseDto = modelMapper.map(updatedSupport, SupportResponseDto.class);
      return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Query set to unresolved");
    }
  }

  @Override
  @Transactional
  public ResponseWrapper<String> deleteSupport(String supportId) {
    Support support = supportRepo.findByIdAndIsDeletedFalse(supportId)
        .orElseThrow(() -> {
          log.error("Support entry not found or already deleted for ID: {}", supportId);
          return new SupportNotFoundException("Support entry not found for ID: " + supportId);
        });

      // Delete the support entry from the repository
      support.setDeleted(true);
      support.setLastModified(LocalDateTime.now());
      supportRepo.save(support);
      log.info("Support entry deleted successfully for user: {}", support.getName());

      // Return a "No Content" response with HTTP status 204
      return ResponseWrapper.success(HttpStatus.OK.value(), "Support id: " +supportId ,"Support query deleted successfully.");
  }
}
