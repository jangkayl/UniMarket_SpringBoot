package com.appdevf2.maiteam.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.appdevf2.maiteam.dto.LoanRequestDTO;
import com.appdevf2.maiteam.entity.LoanRequest;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.LoanRequestService;

@RestController
@RequestMapping("/api/loans") 
public class LoanRequestController {
    private final LoanRequestService loanRequestService;

    public LoanRequestController(LoanRequestService loanRequestService) {
        this.loanRequestService = loanRequestService;
    }

    @GetMapping("/getAllLoans")
    public List<LoanRequestDTO> getAllLoans() {
        return loanRequestService.getAllLoanRequests().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/postLoan")
    public LoanRequestDTO addLoan(@RequestBody LoanRequestDTO dto) {
        LoanRequest loanRequest = convertToEntity(dto);
        
        LoanRequest saved = loanRequestService.saveLoanRequest(loanRequest);
        
        return convertToDTO(saved);
    }

    @PutMapping("/updateLoan/{id}")
    public LoanRequestDTO updateLoan(@PathVariable Long id, @RequestBody LoanRequestDTO dto) {
        LoanRequest newData = convertToEntity(dto);
        LoanRequest updated = loanRequestService.updateLoan(id, newData);
        return updated != null ? convertToDTO(updated) : null;
    }

    @DeleteMapping("/deleteLoan/{id}")
    public String deleteLoan(@PathVariable Long id) {
        boolean deleted = loanRequestService.deleteLoan(id);
        return deleted ? "Loan deleted successfully." : "Loan not found.";
    }

    // --- Helper: Entity to DTO ---
    private LoanRequestDTO convertToDTO(LoanRequest entity) {
        LoanRequestDTO dto = new LoanRequestDTO();
        dto.setLoanid(entity.getLoanid());
        dto.setLoanamount(entity.getLoanamount());
        dto.setRepaymentperioddays(entity.getRepaymentperioddays());
        dto.setInterestrate(entity.getInterestrate());
        dto.setStatus(entity.getStatus());
        dto.setRequestdate(entity.getRequestdate());
        dto.setApprovaldate(entity.getApprovaldate());
        dto.setDuedate(entity.getDuedate());
        dto.setRepaymentdate(entity.getRepaymentdate());
        dto.setPurpose(entity.getPurpose());

        if (entity.getBorrower() != null) {
            dto.setBorrowerId(entity.getBorrower().getStudentId());
        }
        if (entity.getLender() != null) {
            dto.setLenderId(entity.getLender().getStudentId());
        }
        return dto;
    }

    // --- Helper: DTO to Entity ---
    private LoanRequest convertToEntity(LoanRequestDTO dto) {
        LoanRequest entity = new LoanRequest();
        entity.setLoanamount(dto.getLoanamount());
        entity.setRepaymentperioddays(dto.getRepaymentperioddays());
        entity.setInterestrate(dto.getInterestrate());
        entity.setStatus(dto.getStatus());
        entity.setRequestdate(dto.getRequestdate());
        entity.setApprovaldate(dto.getApprovaldate());
        entity.setDuedate(dto.getDuedate());
        entity.setRepaymentdate(dto.getRepaymentdate());
        entity.setPurpose(dto.getPurpose());

        if (dto.getBorrowerId() != null) {
            Student borrower = new Student();
            borrower.setStudentId(dto.getBorrowerId());
            entity.setBorrower(borrower);
        }
        
        if (dto.getLenderId() != null) {
            Student lender = new Student();
            lender.setStudentId(dto.getLenderId());
            entity.setLender(lender);
        }

        return entity;
    }
}