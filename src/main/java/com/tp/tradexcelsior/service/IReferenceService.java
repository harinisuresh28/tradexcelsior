package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.request.ReferenceRequestDto;
import com.tp.tradexcelsior.dto.response.ReferenceResponseDto;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IReferenceService {
    ResponseWrapper<List<ReferenceResponseDto>> getAllReferences();
    ResponseWrapper<ReferenceResponseDto> getReferenceById(String id);
    ResponseWrapper<ReferenceResponseDto> createReference(ReferenceRequestDto referenceRequestDto, MultipartFile image);
    ResponseWrapper<ReferenceResponseDto> updateReference(String id, ReferenceRequestDto referenceRequestDto, MultipartFile image);
    ResponseWrapper<String> deleteReference(String id);
}
