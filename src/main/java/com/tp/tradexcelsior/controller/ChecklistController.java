package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.response.ChecklistDTO;
import com.tp.tradexcelsior.service.impl.ChecklistService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Checklist Management", description = "Endpoints for managing checklists")
@RestController
@RequestMapping("/api/v1/checklist")
public class ChecklistController {
    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @Operation(summary = "Add a new checklist")
    @PostMapping
    public ResponseEntity<ResponseWrapper<ChecklistDTO>> createChecklist(@RequestBody @Valid ChecklistDTO checklistDTO) {
        ResponseWrapper<ChecklistDTO> response = checklistService.addChecklist(checklistDTO);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all checklists")
    // Get All Checklists
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ChecklistDTO>>> getAllChecklists() {
        ResponseWrapper<List<ChecklistDTO>> allChecklists = checklistService.getAllChecklists();
        return ResponseEntity.ok(allChecklists);
    }

    @Operation(summary = "Get a checklist by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ChecklistDTO>> getChecklistById(@PathVariable String id) {
        ResponseWrapper<ChecklistDTO> checklistById = checklistService.getChecklistById(id);
        return ResponseEntity.ok(checklistById);
    }

    @Operation(summary = "Update a checklist")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ChecklistDTO>> updateChecklist(@PathVariable String id, @RequestBody ChecklistDTO updatedChecklist) {
        ResponseWrapper<ChecklistDTO> response = checklistService.updateChecklist(id, updatedChecklist);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a checklist")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteChecklist(@PathVariable String id) {
        ResponseWrapper<String> response = checklistService.deleteChecklist(id);
        return ResponseEntity.ok(response);
    }
}
