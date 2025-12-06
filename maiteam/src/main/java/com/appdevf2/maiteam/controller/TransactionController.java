package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.TransactionDTO;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.service.TransactionService;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/addTransaction")
    public TransactionDTO addTransaction(@RequestBody TransactionDTO dto) {
        Transaction transaction = convertToEntity(dto);
        Transaction saved = transactionService.save(transaction);
        return convertToDTO(saved);
    }

    @GetMapping("/getAllTransactions")
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/updateTransaction/{id}")
    public TransactionDTO updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto) {
        Transaction transaction = convertToEntity(dto);
        Transaction updated = transactionService.update(id, transaction);
        return convertToDTO(updated);
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteById(id);
    }

    // --- Helper: Entity to DTO ---
    private TransactionDTO convertToDTO(Transaction entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(entity.getTransactionId());
        dto.setAmount(entity.getAmount());
        dto.setTransactionType(entity.getTransactionType());
        dto.setStatus(entity.getStatus());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setDueDate(entity.getDueDate());
        dto.setNotes(entity.getNotes());

        if (entity.getBuyer() != null) dto.setBuyerId(entity.getBuyer().getStudentId());
        if (entity.getSeller() != null) dto.setSellerId(entity.getSeller().getStudentId());
        if (entity.getItem() != null) dto.setItemId(entity.getItem().getItemId());

        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Transaction convertToEntity(TransactionDTO dto) {
        Transaction entity = new Transaction();
        entity.setAmount(dto.getAmount());
        entity.setTransactionType(dto.getTransactionType());
        entity.setStatus(dto.getStatus());
        entity.setTransactionDate(dto.getTransactionDate());
        entity.setDueDate(dto.getDueDate());
        entity.setNotes(dto.getNotes());

        if (dto.getBuyerId() != null) {
            Student buyer = new Student();
            buyer.setStudentId(dto.getBuyerId());
            entity.setBuyer(buyer);
        }
        if (dto.getSellerId() != null) {
            Student seller = new Student();
            seller.setStudentId(dto.getSellerId());
            entity.setSeller(seller);
        }
        if (dto.getItemId() != null) {
            Item item = new Item();
            item.setItemId(dto.getItemId());
            entity.setItem(item);
        }

        return entity;
    }
}