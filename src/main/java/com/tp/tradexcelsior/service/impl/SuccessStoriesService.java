package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.dto.request.SuccessStoriesRequestDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SuccessStoriesResponseDto;
import com.tp.tradexcelsior.entity.SuccessStories;
import com.tp.tradexcelsior.exception.custom.SuccessStoryAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.SuccessStoryNotFoundException;
import com.tp.tradexcelsior.repo.SuccessStoriesRepo;
import com.tp.tradexcelsior.service.ISuccessStoriesService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class SuccessStoriesService implements ISuccessStoriesService {

  @Autowired
  private SuccessStoriesRepo successStoriesRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ImageService imageService;

  @Override
  @Transactional
  public ResponseWrapper<SuccessStoriesResponseDto> addSuccessStories(SuccessStoriesRequestDto successStoriesRequestDto) {
    // Check if a success story with the same userName already exists
    successStoriesRepo.findByUserNameAndIsDeletedFalse(successStoriesRequestDto.getUserName())
        .ifPresent(existingStory -> {
          throw new SuccessStoryAlreadyExistsException("A success story with this user name already exists.");
        });

    // Map the DTO to the SuccessStories entity
    SuccessStories successStories = modelMapper.map(successStoriesRequestDto, SuccessStories.class);

    // Save the SuccessStories entity
    SuccessStories savedSuccessStories = successStoriesRepo.save(successStories);

    // Log the successful save and return the response DTO
    log.info("New success story added successfully for user: {}", savedSuccessStories.getUserName());
    byte[] imageByName = imageService.getImageByName(savedSuccessStories.getUserName());
    SuccessStoriesResponseDto successStoriesResponseDto = modelMapper.map(successStories, SuccessStoriesResponseDto.class);

    return ResponseWrapper.success(HttpStatus.CREATED.value(), successStoriesResponseDto, "New success story added.");
  }

  @Override
  @Transactional
  public ResponseWrapper<SuccessStoriesResponseDto> addSuccessStoriesWithImage(SuccessStoriesRequestDto successStoriesRequestDto, MultipartFile image) {
    // Check if a success story with the same userName already exists
    successStoriesRepo.findByUserNameAndIsDeletedFalse(successStoriesRequestDto.getUserName())
        .ifPresent(existingStory -> {
          throw new SuccessStoryAlreadyExistsException("A success story with this user name already exists.");
        });

    // Map the DTO to the SuccessStories entity
    SuccessStories successStories = modelMapper.map(successStoriesRequestDto, SuccessStories.class);

    if(image!=null) {
      Map<String, String> imageByName = null;

      try {
        imageByName = imageService.uploadImageWithCustomName(image,
            successStories.getUserName() + "_success_story");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      successStories.setImageId(imageByName.get("imageId"));
    }

    // Save the SuccessStories entity
    SuccessStories savedSuccessStories = successStoriesRepo.save(successStories);

    // Log the successful save and return the response DTO
    log.info("New success story added successfully for user: {}", savedSuccessStories.getUserName());

    SuccessStoriesResponseDto successStoriesResponseDto = modelMapper.map(successStories, SuccessStoriesResponseDto.class);

    return ResponseWrapper.success(HttpStatus.CREATED.value(), successStoriesResponseDto, "New success story added.");
  }

  @Override
  public ResponseWrapper<SuccessStoriesResponseDto> getSuccessStoryByUserName(String userName) {
    SuccessStories successStories = successStoriesRepo.findByUserNameAndIsDeletedFalse(userName)
        .orElseThrow(() -> new SuccessStoryNotFoundException("Success story with user name " + userName + " not found."));

    SuccessStoriesResponseDto successStoriesResponseDto = modelMapper.map(successStories, SuccessStoriesResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), successStoriesResponseDto, "Success story fetched.");
  }

  @Override
  public ResponseWrapper<PagedResponse<SuccessStoriesResponseDto>> getAllSuccessStories(int page, int size) {
    // If the page number is negative, throw an exception
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }

    // If the page number is negative, throw an exception
    if (size < 1) {
      throw new IllegalArgumentException("Page size can't be less than 1");
    }

    // Count the total number of success stories
    long totalItems = successStoriesRepo.countByIsDeletedFalse();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // Handle the case where the requested page exceeds available pages
    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<SuccessStoriesResponseDto> pagedResponse= new PagedResponse<>(List.of(), (int) totalItems, totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of success stories.");
    }

    // Create a Pageable object with the given page and size
    Pageable pageable = PageRequest.of(page, size);

    // Fetch the paginated data
    Page<SuccessStories> successStoriesPage = successStoriesRepo.findByIsDeletedFalse(pageable);

    // Map the entities to DTOs
    List<SuccessStoriesResponseDto> successStoriesResponseDtoList = successStoriesPage.getContent().stream()
        .map(successStory -> modelMapper.map(successStory, SuccessStoriesResponseDto.class))
        .toList();

    // Log the info
    log.info("Fetched {} success stories, page {} of {}.", successStoriesResponseDtoList.size(), page, totalPages);

    // Return the paginated response
    PagedResponse<SuccessStoriesResponseDto> pagedResponse = new PagedResponse<>(successStoriesResponseDtoList, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of success stories.");
  }

//  @Override
  @Transactional
  public ResponseWrapper<SuccessStoriesResponseDto> updateSuccessStory(String userName, SuccessStoriesRequestDto successStoriesRequestDto, MultipartFile image) {
    // Fetch the existing success story
    SuccessStories existingSuccessStory = successStoriesRepo.findByUserNameAndIsDeletedFalse(userName)
        .orElseThrow(() -> new SuccessStoryNotFoundException("Success story with user name " + userName + " not found."));


    // Check if the userName is being modified (it should not be)
    if (!existingSuccessStory.getUserName().equals(successStoriesRequestDto.getUserName())) {
      String errorMessage = "userName: User name cannot be changed;";

      // Throw a custom validation exception or handle the error as per your requirements
      throw new IllegalArgumentException(errorMessage);
    }

    // Update the rest of the fields
    existingSuccessStory.setFeedback(successStoriesRequestDto.getFeedback());
    existingSuccessStory.setVideoUrl(successStoriesRequestDto.getVideoUrl());
    existingSuccessStory.setTagline(successStoriesRequestDto.getTagline());

    if(image!=null) {
      Map<String, String> imageByName = null;

      try {
        imageByName = imageService.uploadImageWithCustomName(image,
            existingSuccessStory.getUserName() + "_success_story");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      existingSuccessStory.setImageId(imageByName.get("imageId"));
    }

    // Save the updated success story
    existingSuccessStory.setLastModified(LocalDateTime.now());
    SuccessStories updatedSuccessStory = successStoriesRepo.save(existingSuccessStory);

    // Log the success and return the updated SuccessStoriesDto
    log.info("Success story updated successfully for user: {}", updatedSuccessStory.getUserName());

    SuccessStoriesResponseDto successStoriesResponseDto = modelMapper.map(updatedSuccessStory, SuccessStoriesResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), successStoriesResponseDto, "Success story updated.");
  }


  @Override
  @Transactional
  public ResponseWrapper<String> deleteSuccessStory(String userName) {
    // Fetch the SuccessStory entity by userName
    SuccessStories successStories = successStoriesRepo.findByUserNameAndIsDeletedFalse(userName)
        .orElseThrow(() -> new SuccessStoryNotFoundException("Success story with user name " + userName + " not found."));

    successStories.setDeleted(true);
    successStories.setLastModified(LocalDateTime.now());
    successStoriesRepo.save(successStories);
    log.info("Success story deleted successfully for user: {}", userName);

    return ResponseWrapper.success(HttpStatus.OK.value(),"Success story of: "+ userName, "Success story deleted successfully.");
  }
}