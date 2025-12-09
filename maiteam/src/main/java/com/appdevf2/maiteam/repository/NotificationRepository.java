package com.appdevf2.maiteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appdevf2.maiteam.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Custom query to find notifications by specific user
    List<Notification> findByStudent_StudentId(Long studentId);

    List<Notification> findByStudent_StudentIdOrderByCreatedAtDesc(Long studentId);
}
