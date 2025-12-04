package com.appdevf2.maiteam.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appdevf2.maiteam.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find all reviews written BY a specific student
    List<Review> findByReviewer_StudentId(Long studentId);

    // Find all reviews written ABOUT a specific student
    List<Review> findByReviewee_StudentId(Long studentId);

    // Find reviews linked to a specific transaction
    List<Review> findByTransaction_TransactionId(Long transactionId);
}
