package com.appdevf2.maiteam.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "badge")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badge_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", referencedColumnName = "studentId")
    private Student student;

    private LocalDateTime earned_at;

    private String badge_name;
    private String description;
    private String badge_icon;
    private String badge_type;
    private int points_required;

    public Badge() {
        this.earned_at = LocalDateTime.now();
    }

    public Badge(Student student, String badge_name, String description, String badge_icon, String badge_type, int points_required) {
        this.student = student;
        this.badge_name = badge_name;
        this.description = description;
        this.badge_icon = badge_icon;
        this.badge_type = badge_type;
        this.points_required = points_required;
        this.earned_at = LocalDateTime.now();
    }

    public Long getBadge_id() { return badge_id; }
    public void setBadge_id(Long badge_id) { this.badge_id = badge_id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDateTime getEarned_at() { return earned_at; }
    public void setEarned_at(LocalDateTime earned_at) { this.earned_at = earned_at; }

    public String getBadge_name() { return badge_name; }
    public void setBadge_name(String badge_name) { this.badge_name = badge_name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBadge_icon() { return badge_icon; }
    public void setBadge_icon(String badge_icon) { this.badge_icon = badge_icon; }

    public String getBadge_type() { return badge_type; }
    public void setBadge_type(String badge_type) { this.badge_type = badge_type; }

    public int getPoints_required() { return points_required; }
    public void setPoints_required(int points_required) { this.points_required = points_required; }
}