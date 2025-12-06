package com.appdevf2.maiteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appdevf2.maiteam.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Find payments made BY a student
    List<Payment> findByPayer_StudentId(Long studentId);

    // Find payments received BY a student
    List<Payment> findByPayee_StudentId(Long studentId);

    // Find payments linked to a specific transaction
    List<Payment> findByTransaction_TransactionId(Long transactionId);

    // Find payments linked to a specific loan
    List<Payment> findByLoanRequest_Loanid(Long loanId);
}