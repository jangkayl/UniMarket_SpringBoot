package com.appdevf2.maiteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appdevf2.maiteam.entity.Dispute;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    // Find disputes filed BY a specific student
    List<Dispute> findByComplainant_StudentId(Long studentId);

    // Find disputes filed AGAINST a specific student
    List<Dispute> findByRespondent_StudentId(Long studentId);

    // Find the dispute linked to a specific transaction
    List<Dispute> findByTransaction_TransactionId(Long transactionId);
}