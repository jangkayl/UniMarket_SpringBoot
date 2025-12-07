package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.entity.Wallet;
import com.appdevf2.maiteam.entity.WalletTransaction;
import com.appdevf2.maiteam.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long studentId) {
        return ResponseEntity.ok(walletService.getWalletByStudentId(studentId));
    }

    @GetMapping("/{studentId}/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable Long studentId) {
        return ResponseEntity.ok(walletService.getTransactions(studentId));
    }

    // --- PIN Endpoint ---
    @PutMapping("/{studentId}/pin")
    public ResponseEntity<String> setPin(@PathVariable Long studentId, @RequestBody Map<String, String> payload) {
        String newPin = payload.get("newPin");
        String oldPin = payload.get("oldPin");
        
        if (newPin == null || newPin.length() != 4) {
            return ResponseEntity.badRequest().body("Invalid PIN format. Must be 4 digits.");
        }
        
        try {
            walletService.setWalletPin(studentId, newPin, oldPin);
            return ResponseEntity.ok("PIN updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Add Funds
    @PostMapping("/{studentId}/add-funds")
    public ResponseEntity<?> addFunds(@PathVariable Long studentId, @RequestBody Map<String, Object> payload) {
        Double amount = Double.valueOf(payload.get("amount").toString());
        String desc = (String) payload.get("description");
        String refNum = (String) payload.get("referenceNumber");
        
        WalletTransaction tx = walletService.addFunds(studentId, amount, desc, refNum);
        return ResponseEntity.ok(tx);
    }

    // Withdraw Funds 
    @PostMapping("/{studentId}/withdraw")
    public ResponseEntity<?> withdrawFunds(@PathVariable Long studentId, @RequestBody Map<String, Object> payload) {
        try {
            Double amount = Double.valueOf(payload.get("amount").toString());
            String provider = (String) payload.get("provider");
            String accountNumber = (String) payload.get("accountNumber");
            String pin = (String) payload.get("pin");
            
            WalletTransaction tx = walletService.withdrawFunds(studentId, amount, provider, accountNumber, pin);
            return ResponseEntity.ok(tx);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}