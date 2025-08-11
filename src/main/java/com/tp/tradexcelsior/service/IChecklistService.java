package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.response.ChecklistDTO;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.util.List;

public interface IChecklistService {
    ResponseWrapper<ChecklistDTO> addChecklist(ChecklistDTO checklistDTO);
    ResponseWrapper<List<ChecklistDTO>> getAllChecklists();
    ResponseWrapper<ChecklistDTO> getChecklistById(String id);
    ResponseWrapper<ChecklistDTO> updateChecklist(String id, ChecklistDTO updatedChecklist);
    ResponseWrapper<String> deleteChecklist(String id);

}
