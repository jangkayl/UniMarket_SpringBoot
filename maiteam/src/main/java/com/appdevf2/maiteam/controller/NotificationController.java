package com.appdevf2.maiteam.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.appdevf2.maiteam.dto.NotificationDTO;
import com.appdevf2.maiteam.entity.Notification;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/addNotification")
    public NotificationDTO createNotification(@RequestBody NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setRead(dto.isRead());
        
        Student student = new Student();
        student.setStudentId(dto.getStudentId());
        notification.setStudent(student);

        Notification saved = notificationService.createNotification(notification);
        return convertToDTO(saved);
    }

    @GetMapping("/getAllNotifications")
    public List<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getNotificationsByUser/{userId}")
    public List<NotificationDTO> getNotificationsByUser(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/read")
    public NotificationDTO markAsRead(@PathVariable Long id) {
        return convertToDTO(notificationService.markAsRead(id));
    }

    @DeleteMapping("/deleteNotification/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    // --- Helper Method to Convert Entity to DTO ---
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        
        // Extract ONLY the ID from the student object
        if (notification.getStudent() != null) {
            dto.setStudentId(notification.getStudent().getStudentId());
        }
        
        return dto;
    }
}