package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByUniversityEmail(email)
                .orElse(null); 
    }

    public Student login(String email, String password) {
        // 1. Find by email
        Optional<Student> studentOpt = studentRepository.findByUniversityEmail(email);
        
        // 2. Check if student exists AND password matches
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getPasswordHash().equals(password)) {
                return student; 
            }
        }
        return null; 
    }

    public Student update(Long id, Student newData) {
    return studentRepository.findById(id)
            .map(student -> {
                student.setStudentNumber(newData.getStudentNumber());
                student.setUniversityEmail(newData.getUniversityEmail());
                student.setFirstName(newData.getFirstName());
                student.setLastName(newData.getLastName());
                student.setPasswordHash(newData.getPasswordHash());
                student.setPhoneNumber(newData.getPhoneNumber());
                student.setProfilePicture(newData.getProfilePicture());
                student.setIsVerified(newData.getIsVerified());
                student.setAccountStatus(newData.getAccountStatus());
                return studentRepository.save(student);
            })
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id " + id);
        }
        studentRepository.deleteById(id);
    }

}
