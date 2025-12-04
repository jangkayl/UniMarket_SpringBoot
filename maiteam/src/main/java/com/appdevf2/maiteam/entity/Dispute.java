package com.appdevf2.maiteam.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "DISPUTE")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispute_id")
    private Long disputeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "complainant_id", referencedColumnName = "studentId", nullable = false)
    private Student complainant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "respondent_id", referencedColumnName = "studentId", nullable = false)
    private Student respondent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transactionId", nullable = false)
    private Transaction transaction;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status; // e.g., "OPEN", "RESOLVED", "PENDING"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "is_flagged")
    private boolean isFlagged = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "OPEN";
    }

    public Dispute() {}

    // Getters and Setters
    public Long getDisputeId() { return disputeId; }
    public void setDisputeId(Long disputeId) { this.disputeId = disputeId; }

    public Student getComplainant() { return complainant; }
    public void setComplainant(Student complainant) { this.complainant = complainant; }

    public Student getRespondent() { return respondent; }
    public void setRespondent(Student respondent) { this.respondent = respondent; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { isFlagged = flagged; }
}