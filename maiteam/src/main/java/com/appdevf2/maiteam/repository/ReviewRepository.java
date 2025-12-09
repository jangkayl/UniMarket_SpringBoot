package com.appdevf2.maiteam.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appdevf2.maiteam.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find reviews written BY a student (The reviews they GAVE)
    List<Review> findByReviewer_StudentId(Long studentId);

    // Find reviews written ABOUT a student (The reviews they RECEIVED)
    // This is what the /profile/reviews page uses
    List<Review> findByReviewee_StudentId(Long studentId);

    // Find reviews for a specific transaction
    List<Review> findByTransaction_TransactionId(Long transactionId);

    // Check if a specific user has already reviewed a specific transaction
    boolean existsByTransaction_TransactionIdAndReviewer_StudentId(Long transactionId, Long reviewerId);
}
