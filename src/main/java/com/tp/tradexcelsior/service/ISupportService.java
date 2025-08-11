package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.request.SupportRequestDto;
import com.tp.tradexcelsior.dto.request.SupportUpdateDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.SupportResponseDto;
import com.tp.tradexcelsior.util.ResponseWrapper;

public interface ISupportService {
  ResponseWrapper<SupportResponseDto> addSupport(SupportRequestDto supportRequestDto);
  ResponseWrapper<SupportResponseDto> getSupport(String supportId);
  ResponseWrapper<PagedResponse<SupportResponseDto>> getAllSupport(int page, int size);
  ResponseWrapper<PagedResponse<SupportResponseDto>> getResolvedSupport(int page, int size);
  ResponseWrapper<PagedResponse<SupportResponseDto>> getUnresolvedSupport(int page, int size);
  ResponseWrapper<SupportResponseDto> updateSupport(SupportUpdateDto updateDto);
  ResponseWrapper<String> deleteSupport(String supportId);
}
