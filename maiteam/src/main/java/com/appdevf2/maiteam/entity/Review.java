package com.appdevf2.maiteam.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "studentId")
    private Student reviewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewee_id", referencedColumnName = "studentId")
    private Student reviewee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transactionId")
    private Transaction transaction;

    private int rating;
    private String comment;
    private LocalDateTime created_at;
    private boolean is_flagged;

    // Constructors
    public Review() {
        this.created_at = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getReview_id() { return review_id; }
    public void setReview_id(Long review_id) { this.review_id = review_id; }

    public Student getReviewer() { return reviewer; }
    public void setReviewer(Student reviewer) { this.reviewer = reviewer; }

    public Student getReviewee() { return reviewee; }
    public void setReviewee(Student reviewee) { this.reviewee = reviewee; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public boolean isIs_flagged() { return is_flagged; }
    public void setIs_flagged(boolean is_flagged) { this.is_flagged = is_flagged; }
}