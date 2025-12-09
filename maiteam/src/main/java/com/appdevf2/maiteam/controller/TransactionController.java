package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.TransactionDTO;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
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

    // --- Uses the new "smart" active transaction logic ---
    @GetMapping("/active")
    public ResponseEntity<TransactionDTO> getActiveTransaction(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        return transactionService.getActiveTransaction(userId1, userId2)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<TransactionDTO>> getPendingTransactions(@PathVariable Long userId) {
        List<TransactionDTO> pending = transactionService.getPendingTransactionsForUser(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactionHistory(@PathVariable Long userId) {
        List<TransactionDTO> history = transactionService.getUserHistory(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        try {
            transactionService.cancelTransaction(id, userId);
            return ResponseEntity.ok(Map.of("message", "Transaction cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

     // --- Accept Order ---
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptTransaction(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            transactionService.acceptTransaction(id, payload.get("userId"));
            return ResponseEntity.ok(Map.of("message", "Order accepted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Confirm Receipt ---
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmTransaction(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            transactionService.confirmTransaction(id, payload.get("userId"));
            return ResponseEntity.ok(Map.of("message", "Transaction confirmed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Return Item ---
    @PostMapping("/{id}/return")
    public ResponseEntity<?> markAsReturned(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            transactionService.markAsReturned(id, payload.get("userId"));
            return ResponseEntity.ok(Map.of("message", "Return initiated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Complete Return ---
    @PostMapping("/{id}/complete-return")
    public ResponseEntity<?> completeReturn(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            transactionService.completeReturn(id, payload.get("userId"));
            return ResponseEntity.ok(Map.of("message", "Return confirmed, transaction closed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Helpers ---
    private TransactionDTO convertToDTO(Transaction entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(entity.getTransactionId());
        dto.setAmount(entity.getAmount());
        dto.setTransactionType(entity.getTransactionType());
        dto.setStatus(entity.getStatus());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setDueDate(entity.getDueDate());
        dto.setNotes(entity.getNotes());
        if (entity.getBuyer() != null) { dto.setBuyerId(entity.getBuyer().getStudentId()); dto.setBuyerName(entity.getBuyer().getFirstName() + " " + entity.getBuyer().getLastName()); }
        if (entity.getSeller() != null) { dto.setSellerId(entity.getSeller().getStudentId()); dto.setSellerName(entity.getSeller().getFirstName() + " " + entity.getSeller().getLastName()); }
        if (entity.getItem() != null) { dto.setItemId(entity.getItem().getItemId()); dto.setItemName(entity.getItem().getItemName()); dto.setItemImage(entity.getItem().getItemPhoto()); }
        return dto;
    }

    private Transaction convertToEntity(TransactionDTO dto) {
        Transaction entity = new Transaction();
        entity.setAmount(dto.getAmount());
        entity.setTransactionType(dto.getTransactionType());
        entity.setStatus(dto.getStatus());
        entity.setTransactionDate(dto.getTransactionDate());
        entity.setDueDate(dto.getDueDate());
        entity.setNotes(dto.getNotes());
        if (dto.getBuyerId() != null) { Student s = new Student(); s.setStudentId(dto.getBuyerId()); entity.setBuyer(s); }
        if (dto.getSellerId() != null) { Student s = new Student(); s.setStudentId(dto.getSellerId()); entity.setSeller(s); }
        if (dto.getItemId() != null) { Item i = new Item(); i.setItemId(dto.getItemId()); entity.setItem(i); }
        return entity;
    }
}