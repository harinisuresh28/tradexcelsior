package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.request.SuccessStoriesRequestDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SuccessStoriesResponseDto;
import com.tp.tradexcelsior.util.ResponseWrapper;
import org.springframework.web.multipart.MultipartFile;

public interface ISuccessStoriesService {
  ResponseWrapper<SuccessStoriesResponseDto> addSuccessStories(SuccessStoriesRequestDto successStoriesRequestDto);
  ResponseWrapper<SuccessStoriesResponseDto> addSuccessStoriesWithImage(SuccessStoriesRequestDto successStoriesRequestDto, MultipartFile image);
  ResponseWrapper<SuccessStoriesResponseDto> getSuccessStoryByUserName(String userName);
  ResponseWrapper<PagedResponse<SuccessStoriesResponseDto>> getAllSuccessStories(int page, int size);
  ResponseWrapper<SuccessStoriesResponseDto> updateSuccessStory(String userName, SuccessStoriesRequestDto successStoriesRequestDto, MultipartFile image);
  ResponseWrapper<String> deleteSuccessStory(String userName);
}
