package com.appdevf2.maiteam.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.appdevf2.maiteam.dto.BadgeDTO;
import com.appdevf2.maiteam.entity.Badge;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping("/addBadge")
    public BadgeDTO addBadge(@RequestBody BadgeDTO dto) {
        Badge badge = convertToEntity(dto);
        Badge savedBadge = badgeService.saveBadge(badge);
        return convertToDTO(savedBadge);
    }

    @GetMapping("/getAllBadges")
    public List<BadgeDTO> getAllBadge() {
        return badgeService.getAllBadge().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/deleteAllBadge")
    public void deleteAllBadge(){
        badgeService.deleteAllBadge();
    }

    @DeleteMapping("/deleteBadge/{id}")
    public void deleteBadge(@PathVariable Long id){
        badgeService.deleteBadge(id);
    }

    @PutMapping("/updateBadge/{id}")
    public BadgeDTO updateBadge(@PathVariable Long id, @RequestBody BadgeDTO dto) {
        Badge badge = convertToEntity(dto);
        Badge updated = badgeService.updateBadge(id, badge);
        return convertToDTO(updated);
    }
    
    // --- Helper: Entity to DTO ---
    private BadgeDTO convertToDTO(Badge entity) {
        BadgeDTO dto = new BadgeDTO();
        dto.setBadge_id(entity.getBadge_id());
        dto.setBadge_name(entity.getBadge_name());
        dto.setDescription(entity.getDescription());
        dto.setBadge_icon(entity.getBadge_icon());
        dto.setBadge_type(entity.getBadge_type());
        dto.setPoints_required(entity.getPoints_required());
        dto.setEarned_at(entity.getEarned_at());
        
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getStudentId());
        }
        
        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Badge convertToEntity(BadgeDTO dto) {
        Badge entity = new Badge();
        entity.setBadge_name(dto.getBadge_name());
        entity.setDescription(dto.getDescription());
        entity.setBadge_icon(dto.getBadge_icon());
        entity.setBadge_type(dto.getBadge_type());
        entity.setPoints_required(dto.getPoints_required());
        
        if (dto.getStudentId() != null) {
            Student student = new Student();
            student.setStudentId(dto.getStudentId());
            entity.setStudent(student);
        }
        
        return entity;
    }
}