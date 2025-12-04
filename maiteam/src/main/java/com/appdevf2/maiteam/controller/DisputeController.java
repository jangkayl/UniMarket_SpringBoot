package com.appdevf2.maiteam.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.appdevf2.maiteam.dto.DisputeDTO;
import com.appdevf2.maiteam.entity.Dispute;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.service.DisputeService;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    @PostMapping("/addDispute")
    public DisputeDTO createDispute(@RequestBody DisputeDTO dto) {
        Dispute dispute = convertToEntity(dto);
        Dispute savedDispute = disputeService.createDispute(dispute);
        return convertToDTO(savedDispute);
    }

    @GetMapping("/getAllDisputes")
    public List<DisputeDTO> getAllDisputes() {
        return disputeService.getAllDisputes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DisputeDTO getDisputeById(@PathVariable Long id) {
        return disputeService.getDisputeById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @PutMapping("/resolve/{id}")
    public DisputeDTO resolveDispute(@PathVariable Long id, @RequestBody String resolutionNotes) {
        return convertToDTO(disputeService.resolveDispute(id, resolutionNotes));
    }

    @DeleteMapping("/deleteDispute/{id}")
    public void deleteDispute(@PathVariable Long id) {
        disputeService.deleteDispute(id);
    }

    // --- Helper: Entity to DTO ---
    private DisputeDTO convertToDTO(Dispute entity) {
        DisputeDTO dto = new DisputeDTO();
        dto.setDisputeId(entity.getDisputeId());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResolvedAt(entity.getResolvedAt());
        dto.setResolutionNotes(entity.getResolutionNotes());
        dto.setIsFlagged(entity.isFlagged());

        if (entity.getComplainant() != null) dto.setComplainantId(entity.getComplainant().getStudentId());
        if (entity.getRespondent() != null) dto.setRespondentId(entity.getRespondent().getStudentId());
        if (entity.getTransaction() != null) dto.setTransactionId(entity.getTransaction().getTransactionId());

        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Dispute convertToEntity(DisputeDTO dto) {
        Dispute entity = new Dispute();
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        
        if (dto.getComplainantId() != null) {
            Student complainant = new Student();
            complainant.setStudentId(dto.getComplainantId());
            entity.setComplainant(complainant);
        }
        if (dto.getRespondentId() != null) {
            Student respondent = new Student();
            respondent.setStudentId(dto.getRespondentId());
            entity.setRespondent(respondent);
        }
        if (dto.getTransactionId() != null) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(dto.getTransactionId());
            entity.setTransaction(transaction);
        }

        return entity;
    }
}