package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.dto.request.ReferenceRequestDto;
import com.tp.tradexcelsior.dto.response.ReferenceResponseDto;
import com.tp.tradexcelsior.entity.Reference;
import com.tp.tradexcelsior.exception.custom.BookAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ReferenceAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ReferenceNotFoundException;
import com.tp.tradexcelsior.repo.ReferenceRepository;
import com.tp.tradexcelsior.service.IReferenceService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ReferenceService implements IReferenceService {
    @Autowired
    private final ReferenceRepository referenceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    public ReferenceService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    // Get all references
    @Override
    public ResponseWrapper<List<ReferenceResponseDto>> getAllReferences() {
        log.info("Fetching all references.");
        List<Reference> references = referenceRepository.findByIsDeletedFalse();

        log.info("Total references found: {}", references.size());
        List<ReferenceResponseDto> referenceResponseDtoList = references.stream()
                .map(reference -> modelMapper.map(reference, ReferenceResponseDto.class))
                .toList();

        return ResponseWrapper.success(HttpStatus.OK.value(), referenceResponseDtoList, "List of reference.");
    }

    // Get a reference by ID
    @Override
    public ResponseWrapper<ReferenceResponseDto> getReferenceById(String id) {
        log.info("Fetching reference with ID: {}", id);
        Reference reference = referenceRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ReferenceNotFoundException("Reference with ID " + id + " not found."));

        log.info("Reference found with ID: {}", id);

        ReferenceResponseDto referenceResponseDto = modelMapper.map(reference, ReferenceResponseDto.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), referenceResponseDto, "Reference fetched.");
    }

    // Create a new reference
    @Override
    public ResponseWrapper<ReferenceResponseDto> createReference(
        ReferenceRequestDto referenceRequestDto, MultipartFile image) {
        log.info("Attempting to add a new reference: {}", referenceRequestDto.getName());

        // Check for duplicate reference
        referenceRepository.findByNameAndTypeAndIsDeletedFalse(referenceRequestDto.getName(), referenceRequestDto.getType())
                .ifPresent(existingReference -> {
            log.warn("Duplicate reference found: {}", referenceRequestDto.getName());
            throw new ReferenceAlreadyExistException("A reference with the same name and type already exists.");
        });


        // Convert DTO to Entity
        Reference reference = modelMapper.map(referenceRequestDto, Reference.class);

        if(image!=null) {
            Map<String, String> imageByName = null;

            try {
                imageByName = imageService.uploadImageWithCustomName(image, reference.getName() + "_reference");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            reference.setImageId(imageByName.get("imageId"));
        }

        // Save reference
        Reference savedReference = referenceRepository.save(reference);
        log.info("Reference added successfully with ID: {}", savedReference.getId());

        // Convert Entity to DTO and return
        ReferenceResponseDto savedReferenceResponseDto = modelMapper.map(savedReference, ReferenceResponseDto.class);
        return ResponseWrapper.success(HttpStatus.CREATED.value(), savedReferenceResponseDto, "New reference saved.");
    }


    // Update an existing reference
    @Override
    public ResponseWrapper<ReferenceResponseDto> updateReference(String id, ReferenceRequestDto referenceRequestDto, MultipartFile image) {
        log.info("Updating reference with ID: {}", id);

        Reference existingReference = referenceRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ReferenceNotFoundException("Cannot update. Reference with ID " + id + " not found."));

        referenceRequestDto.setId(existingReference.getId());

        existingReference = modelMapper.map(referenceRequestDto, Reference.class);

        if(image!=null) {
            Map<String, String> imageByName = null;

            try {
                imageByName = imageService.uploadImageWithCustomName(image, existingReference.getName() + "_reference");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            existingReference.setImageId(imageByName.get("imageId"));
        }

        existingReference.setLastModified(LocalDateTime.now());
        Reference updatedReference = referenceRepository.save(existingReference);
        log.info("Reference updated successfully with ID: {}", updatedReference.getId());

        ReferenceResponseDto updatedReferenceResponseDto = modelMapper.map(updatedReference, ReferenceResponseDto.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), updatedReferenceResponseDto, "Reference updated.");
    }


    // Delete a reference by ID
    @Override
    public ResponseWrapper<String> deleteReference(String id) {
        log.info("Deleting reference with ID: {}", id);

        Reference reference = referenceRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ReferenceNotFoundException("Cannot delete. Reference with ID " + id + " not found."));

        reference.setDeleted(true);
        reference.setLastModified(LocalDateTime.now());
        referenceRepository.save(reference);
        log.info("Reference deleted successfully with ID: {}", id);

        return ResponseWrapper.success(HttpStatus.OK.value(), "Reference id: " + id, "Reference delete.");
    }
}
