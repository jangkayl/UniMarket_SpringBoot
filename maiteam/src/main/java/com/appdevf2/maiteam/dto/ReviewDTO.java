package com.appdevf2.maiteam.dto;

import java.time.LocalDateTime;

public class ReviewDTO {

    private Long review_id;
    private Long reviewerId;
    private Long revieweeId;
    private Long transactionId;
    private int rating;
    private String comment;
    private LocalDateTime created_at;
    private boolean is_flagged;

    public ReviewDTO() {}

    // Getters and Setters
    public Long getReview_id() { return review_id; }
    public void setReview_id(Long review_id) { this.review_id = review_id; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public Long getRevieweeId() { return revieweeId; }
    public void setRevieweeId(Long revieweeId) { this.revieweeId = revieweeId; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public boolean isIs_flagged() { return is_flagged; }
    public void setIs_flagged(boolean is_flagged) { this.is_flagged = is_flagged; }
}