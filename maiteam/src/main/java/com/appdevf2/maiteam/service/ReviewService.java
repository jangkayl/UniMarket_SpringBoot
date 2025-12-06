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
        // 1. Validate Reviewer
        if (review.getReviewer() != null && review.getReviewer().getStudentId() != null) {
            Student reviewer = studentRepository.findById(review.getReviewer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found with ID: " + review.getReviewer().getStudentId()));
            review.setReviewer(reviewer);
        } else {
            throw new RuntimeException("Reviewer ID is required.");
        }

        // 2. Validate Reviewee
        if (review.getReviewee() != null && review.getReviewee().getStudentId() != null) {
            Student reviewee = studentRepository.findById(review.getReviewee().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Reviewee not found with ID: " + review.getReviewee().getStudentId()));
            review.setReviewee(reviewee);
        } else {
            throw new RuntimeException("Reviewee ID is required.");
        }

        // 3. Validate Transaction (Optional, but if provided must exist)
        if (review.getTransaction() != null && review.getTransaction().getTransactionId() != null) {
            Transaction transaction = transactionRepository.findById(review.getTransaction().getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + review.getTransaction().getTransactionId()));
            review.setTransaction(transaction);
        }

        if(review.getCreated_at() == null) {
            review.setCreated_at(LocalDateTime.now());
        }

        return reviewRepo.save(review);
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