package com.appdevf2.maiteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appdevf2.maiteam.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByStudent_StudentId(Long studentId);
}
