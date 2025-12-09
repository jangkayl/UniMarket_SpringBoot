package com.appdevf2.maiteam.service;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.appdevf2.maiteam.entity.Review;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.repository.ReviewRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.TransactionRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final StudentRepository studentRepository;
    private final TransactionRepository transactionRepository;

    public ReviewService(ReviewRepository reviewRepo, 
                         StudentRepository studentRepository, 
                         TransactionRepository transactionRepository) {
        this.reviewRepo = reviewRepo;
        this.studentRepository = studentRepository;
        this.transactionRepository = transactionRepository;
    }

    public Review saveReview(Review review) {
        // 1. Validate Transaction Existence & Status
        if (review.getTransaction() == null || review.getTransaction().getTransactionId() == null) {
             throw new RuntimeException("Transaction ID is required.");
        }

        Transaction transaction = transactionRepository.findById(review.getTransaction().getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + review.getTransaction().getTransactionId()));

        // Rule: Can only review Completed or Cancelled transactions
        String status = transaction.getStatus() != null ? transaction.getStatus().toLowerCase() : "";
        if (!status.equals("completed") && !status.equals("cancelled")) {
            throw new RuntimeException("You can only review completed or cancelled transactions.");
        }

        // Rule: One review per transaction per person
        if (review.getReviewer() != null && review.getReviewer().getStudentId() != null) {
            boolean alreadyReviewed = reviewRepo.existsByTransaction_TransactionIdAndReviewer_StudentId(
                transaction.getTransactionId(), 
                review.getReviewer().getStudentId()
            );
            if (alreadyReviewed) {
                throw new RuntimeException("You have already reviewed this transaction.");
            }
        }
        
        review.setTransaction(transaction);

        // 2. Validate Reviewer
        if (review.getReviewer() != null && review.getReviewer().getStudentId() != null) {
            Student reviewer = studentRepository.findById(review.getReviewer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found with ID: " + review.getReviewer().getStudentId()));
            review.setReviewer(reviewer);
        } else {
            throw new RuntimeException("Reviewer ID is required.");
        }

        // 3. Validate Reviewee
        if (review.getReviewee() != null && review.getReviewee().getStudentId() != null) {
            Student reviewee = studentRepository.findById(review.getReviewee().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Reviewee not found with ID: " + review.getReviewee().getStudentId()));
            review.setReviewee(reviewee);
        } else {
            throw new RuntimeException("Reviewee ID is required.");
        }

        // Set timestamp if not present
        if(review.getCreated_at() == null) {
            review.setCreated_at(LocalDateTime.now());
        }

        return reviewRepo.save(review);
    }

    // --- NEW: Get Reviews for a specific user (Reviewee) ---
    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepo.findByReviewee_StudentId(userId);
    }

    public List<Review> getAllReview() {
        return reviewRepo.findAll();
    }

    public Review getReviewById(Long id) {
        return reviewRepo.findById(id).orElse(null);
    }

    public void deleteAllReview(){
        reviewRepo.deleteAll();
    }

    public void deleteReview(Long id){
        if(reviewRepo.existsById(id)) {
            reviewRepo.deleteById(id);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }

    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepo.findById(id).map(review -> {
            review.setComment(updatedReview.getComment());
            review.setRating(updatedReview.getRating());
            review.setIs_flagged(updatedReview.isIs_flagged());
            
            if (updatedReview.getReviewer() != null && updatedReview.getReviewer().getStudentId() != null) {
                 Student reviewer = studentRepository.findById(updatedReview.getReviewer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found"));
                 review.setReviewer(reviewer);
            }
            
            return reviewRepo.save(review);
        }).orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }
}