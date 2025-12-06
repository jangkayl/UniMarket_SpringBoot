package com.appdevf2.maiteam.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.appdevf2.maiteam.entity.Badge;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.BadgeRepository;
import com.appdevf2.maiteam.repository.StudentRepository;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepo;
    private final StudentRepository studentRepository;

    public BadgeService(BadgeRepository badgeRepo, StudentRepository studentRepository) {
        this.badgeRepo = badgeRepo;
        this.studentRepository = studentRepository;
    }

    public Badge saveBadge(Badge badge) {
        // Validate and Fetch Student
        if (badge.getStudent() != null && badge.getStudent().getStudentId() != null) {
            Student student = studentRepository.findById(badge.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + badge.getStudent().getStudentId()));
            badge.setStudent(student);
        } else {
             throw new RuntimeException("Student ID is required for a badge");
        }
        return badgeRepo.save(badge);
    }

    public List<Badge> getAllBadge() {
        return badgeRepo.findAll();
    }

    public List<Badge> getBadgesByStudent(Long studentId) {
        return badgeRepo.findByStudent_StudentId(studentId);
    }

    public Badge getBadge(Long id){
        return badgeRepo.findById(id).orElse(null);
    }

    public void deleteAllBadge(){
        badgeRepo.deleteAll();
    }

    public void deleteBadge(Long id){
        badgeRepo.deleteById(id);
    }

    public Badge updateBadge(Long id, Badge updatedBadge) {
        return badgeRepo.findById(id)
            .map(existingBadge -> {
                existingBadge.setBadge_name(updatedBadge.getBadge_name());
                existingBadge.setDescription(updatedBadge.getDescription());
                existingBadge.setBadge_icon(updatedBadge.getBadge_icon());
                existingBadge.setBadge_type(updatedBadge.getBadge_type());
                existingBadge.setPoints_required(updatedBadge.getPoints_required());
                
                // Update Student owner if provided
                if (updatedBadge.getStudent() != null && updatedBadge.getStudent().getStudentId() != null) {
                     Student student = studentRepository.findById(updatedBadge.getStudent().getStudentId())
                        .orElseThrow(() -> new RuntimeException("Student not found"));
                     existingBadge.setStudent(student);
                }

                return badgeRepo.save(existingBadge);
            })
            .orElseThrow(() -> new RuntimeException("Badge not found with id " + id));
    }
}