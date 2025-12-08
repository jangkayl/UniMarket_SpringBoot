package com.appdevf2.maiteam.repository;

import java.util.List;

import com.appdevf2.maiteam.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {    

  // --- UPDATED: Fetch list ordered by ID (proxy for time) Descending ---
    // This allows the service layer to filter through them
    List<Transaction> findByBuyer_StudentIdAndSeller_StudentIdOrBuyer_StudentIdAndSeller_StudentIdOrderByTransactionIdDesc(
            Long buyerId1, Long sellerId1, Long buyerId2, Long sellerId2
    );

    // Sidebar pending logic
    List<Transaction> findByStatusAndBuyer_StudentIdOrStatusAndSeller_StudentId(
            String status1, Long buyerId, String status2, Long sellerId
    );

    // History logic
    List<Transaction> findByBuyer_StudentIdOrSeller_StudentIdOrderByTransactionDateDesc(Long buyerId, Long sellerId);
}
