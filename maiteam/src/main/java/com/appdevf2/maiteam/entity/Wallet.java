package com.appdevf2.maiteam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "studentId", unique = true)
    private Student student;

    private Double balance = 0.00;

    private String pin; 

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}