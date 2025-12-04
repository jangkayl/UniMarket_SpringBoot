package com.appdevf2.maiteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appdevf2.maiteam.entity.LoanRequest;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    // Find loans requested BY a specific student
    List<LoanRequest> findByBorrower_StudentId(Long studentId);

    // Find loans lent BY a specific student (lender)
    List<LoanRequest> findByLender_StudentId(Long studentId);
}

