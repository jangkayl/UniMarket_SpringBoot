package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Wallet;
import com.appdevf2.maiteam.entity.WalletTransaction;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.WalletRepository;
import com.appdevf2.maiteam.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final StudentRepository studentRepository;

    public WalletService(WalletRepository walletRepository, 
                         WalletTransactionRepository transactionRepository,
                         StudentRepository studentRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.studentRepository = studentRepository;
    }

    public Wallet getWalletByStudentId(Long studentId) {
        return walletRepository.findByStudent_StudentId(studentId)
                .orElseGet(() -> createWalletForStudent(studentId));
    }

    private Wallet createWalletForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Wallet wallet = new Wallet();
        wallet.setStudent(student);
        wallet.setBalance(0.0);
        wallet.setUpdatedAt(LocalDateTime.now());
        return walletRepository.save(wallet);
    }

    public List<WalletTransaction> getTransactions(Long studentId) {
        Wallet wallet = getWalletByStudentId(studentId);
        return transactionRepository.findByWallet_WalletIdOrderByTransactionDateDesc(wallet.getWalletId());
    }

    // --- PIN Management ---
    public void setWalletPin(Long studentId, String newPin, String oldPin) {
        Wallet wallet = getWalletByStudentId(studentId);
        
        if (wallet.getPin() != null && !wallet.getPin().isEmpty()) {
            if (oldPin == null || !wallet.getPin().equals(oldPin)) {
                throw new RuntimeException("Incorrect current PIN");
            }
        }
        
        wallet.setPin(newPin); 
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    @Transactional
    public WalletTransaction addFunds(Long studentId, Double amount, String description, String referenceNumber) {
        Wallet wallet = getWalletByStudentId(studentId);
        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setStatus("Completed");
        tx.setDescription(description);
        tx.setReferenceNumber(referenceNumber);
        tx.setTransactionDate(LocalDateTime.now());
        
        return transactionRepository.save(tx);
    }

    // --- NEW: Hold Funds for Purchase ---
    @Transactional
    public void holdFunds(Long studentId, Double amount, String itemName) {
        Wallet wallet = getWalletByStudentId(studentId);

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance for this purchase.");
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record the deduction in history
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setStatus("HOLD"); // Mark as HELD until transaction completes/cancels
        tx.setDescription("Payment held for: " + itemName);
        tx.setTransactionDate(LocalDateTime.now());
        
        transactionRepository.save(tx);
    }

    // --- NEW: Withdraw Funds ---
    @Transactional
    public WalletTransaction withdrawFunds(Long studentId, Double amount, String provider, String accountNumber, String pin) {
        Wallet wallet = getWalletByStudentId(studentId);
        
        if (wallet.getPin() == null || !wallet.getPin().equals(pin)) {
            throw new RuntimeException("Invalid Security PIN");
        }

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setStatus("Completed");
        tx.setDescription("Withdrawal to " + provider + " (" + accountNumber + ")");
        tx.setTransactionDate(LocalDateTime.now());
        
        return transactionRepository.save(tx);
    }
}