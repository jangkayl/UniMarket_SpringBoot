package com.appdevf2.maiteam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    private Double amount;
    private String type; // "CREDIT" (Add) or "DEBIT" (Subtract)
    private String description;
    private String status; // "COMPLETED", "PENDING", "FAILED"
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate = LocalDateTime.now();
}