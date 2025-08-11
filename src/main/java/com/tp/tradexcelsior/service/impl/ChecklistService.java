package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.dto.response.ChecklistDTO;
import com.tp.tradexcelsior.entity.Checklist;
import com.tp.tradexcelsior.exception.custom.ChecklistAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ChecklistNotFoundException;
import com.tp.tradexcelsior.repo.ChecklistRepository;
import com.tp.tradexcelsior.service.IChecklistService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChecklistService implements IChecklistService {

    @Autowired
    private final ChecklistRepository checklistRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    @Override
    public ResponseWrapper<ChecklistDTO> addChecklist(ChecklistDTO checklistDTO) {
        log.info("Attempting to add a new checklist");

        boolean exists = checklistRepository.existsByDescriptionAndButtonNameAndIsDeletedFalse(checklistDTO.getDescription(), checklistDTO.getButtonName());
        if (exists) {
            log.warn("Checklist with description '{}' and button name '{}' already exists",
                    checklistDTO.getDescription(), checklistDTO.getButtonName());
            throw new ChecklistAlreadyExistException("A checklist with the same description already exists.");
        }

        Checklist savedChecklist = checklistRepository.save(modelMapper.map(checklistDTO, Checklist.class));
        log.info("Checklist added successfully with ID: {}", savedChecklist.getId());

        ChecklistDTO savedChecklistDTO = modelMapper.map(savedChecklist, ChecklistDTO.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), savedChecklistDTO, "New checklist saved.");
    }


    @Override
    public ResponseWrapper<List<ChecklistDTO>> getAllChecklists() {
        log.info("Fetching all checklists from the database");
        List<Checklist> checklists = checklistRepository.findByIsDeletedFalse();
        List<ChecklistDTO> checklistDTOS = checklists.stream().map(checklist -> modelMapper.map(checklist, ChecklistDTO.class)).toList();
        return ResponseWrapper.success(HttpStatus.OK.value(), checklistDTOS, "List of checklist.");
    }

    @Override
    public ResponseWrapper<ChecklistDTO> getChecklistById(String id) {
        log.info("Fetching checklist with ID: {}", id);

        Checklist checklist= checklistRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ChecklistNotFoundException("Checklist with ID " + id + " not found."));

        ChecklistDTO checklistDTO = modelMapper.map(checklist, ChecklistDTO.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), checklistDTO, "Checklist fetched.");
    }


    @Override
    public ResponseWrapper<ChecklistDTO> updateChecklist(String id, ChecklistDTO checklistDTO) {
        log.info("Updating checklist with ID: {}", id);

        Checklist existingChecklist = checklistRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ChecklistNotFoundException("Cannot update. Checklist with ID " + id + " not found."));

        // Update fields
        existingChecklist.setDescription(checklistDTO.getDescription());
        existingChecklist.setLink(checklistDTO.getLink());
        existingChecklist.setButtonName(checklistDTO.getButtonName());
        existingChecklist.setLastModified(LocalDateTime.now());

        // Save the updated checklist
        Checklist updatedChecklist = checklistRepository.save(existingChecklist);
        log.info("Checklist updated successfully with ID: {}", id);

        ChecklistDTO updatedChecklistDto = modelMapper.map(updatedChecklist, ChecklistDTO.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), updatedChecklistDto, "Checklist updated");
    }

    @Override
    public ResponseWrapper<String> deleteChecklist(String id) {
        log.info("Deleting checklist with ID: {}", id);

        Checklist checklist = checklistRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ChecklistNotFoundException("Cannot delete. Checklist with ID " + id + " not found."));

        checklist.setDeleted(true);
        checklist.setLastModified(LocalDateTime.now());
        checklistRepository.save(checklist);
        log.info("Checklist deleted successfully with ID: {}", id);

        return ResponseWrapper.success(HttpStatus.OK.value(), "Checklist id: " + id, "Checklist deleted successfully.");
    }

}
