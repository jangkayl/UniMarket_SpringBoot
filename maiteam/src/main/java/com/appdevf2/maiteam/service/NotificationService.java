package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Notification;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.NotificationRepository;
import com.appdevf2.maiteam.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Notification createNotification(Notification notification) {
        if (notification.getStudent() != null && notification.getStudent().getStudentId() != null) {
            
            Student existingStudent = studentRepository.findById(notification.getStudent().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + notification.getStudent().getStudentId()));
            notification.setStudent(existingStudent);
        } else {
            throw new RuntimeException("Error: studentId is required.");
        }
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByStudent_StudentId(userId);
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
