package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.ReviewDTO;
import com.appdevf2.maiteam.entity.Review;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/addReview")
    public ReviewDTO addReview(@RequestBody ReviewDTO dto) {
        Review review = convertToEntity(dto);
        Review savedReview = reviewService.saveReview(review);
        return convertToDTO(savedReview);
    }

    @GetMapping("/getAllReviews")
    public List<ReviewDTO> getAllReviews() {
        return reviewService.getAllReview().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/updateReview/{id}")
    public ReviewDTO updateReview(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review review = convertToEntity(dto);
        Review updated = reviewService.updateReview(id, review);
        return convertToDTO(updated);
    }

    @DeleteMapping("/deleteAllReview")
    public void deleteAllReview(){
        reviewService.deleteAllReview();
    }

    @DeleteMapping("/deleteReview/{id}")
    public void deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
    }

    // --- Helper: Entity to DTO ---
    private ReviewDTO convertToDTO(Review entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReview_id(entity.getReview_id());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setCreated_at(entity.getCreated_at());
        dto.setIs_flagged(entity.isIs_flagged());

        if (entity.getReviewer() != null) dto.setReviewerId(entity.getReviewer().getStudentId());
        if (entity.getReviewee() != null) dto.setRevieweeId(entity.getReviewee().getStudentId());
        if (entity.getTransaction() != null) dto.setTransactionId(entity.getTransaction().getTransactionId());

        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Review convertToEntity(ReviewDTO dto) {
        Review entity = new Review();
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        entity.setIs_flagged(dto.isIs_flagged());
        // entity.setCreated_at(dto.getCreated_at());

        if (dto.getReviewerId() != null) {
            Student reviewer = new Student();
            reviewer.setStudentId(dto.getReviewerId());
            entity.setReviewer(reviewer);
        }
        if (dto.getRevieweeId() != null) {
            Student reviewee = new Student();
            reviewee.setStudentId(dto.getRevieweeId());
            entity.setReviewee(reviewee);
        }
        if (dto.getTransactionId() != null) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(dto.getTransactionId());
            entity.setTransaction(transaction);
        }

        return entity;
    }
}