package com.appdevf2.maiteam.dto;

import java.time.LocalDateTime;

public class BadgeDTO {

    private Long badge_id;
    private Long studentId;
    private LocalDateTime earned_at;
    private String badge_name;
    private String description;
    private String badge_icon;
    private String badge_type;
    private int points_required;

    public BadgeDTO() {}

    public Long getBadge_id() { return badge_id; }
    public void setBadge_id(Long badge_id) { this.badge_id = badge_id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

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