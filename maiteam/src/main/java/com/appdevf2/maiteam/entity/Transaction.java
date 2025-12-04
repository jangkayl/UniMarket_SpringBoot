package com.appdevf2.maiteam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Double amount;
    private String transactionType;
    private String status;
    private LocalDate transactionDate;
    private LocalDate dueDate;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "studentId")
    private Student buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "studentId")
    private Student seller;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "itemId")
    private Item item;
}