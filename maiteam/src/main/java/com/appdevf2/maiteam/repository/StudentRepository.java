package com.appdevf2.maiteam.repository;

import java.util.Optional;

import com.appdevf2.maiteam.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUniversityEmail(String universityEmail);
}
