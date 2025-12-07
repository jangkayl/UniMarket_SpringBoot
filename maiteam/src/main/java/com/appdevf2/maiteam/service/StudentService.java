package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    
    private final Path fileStorageLocation = Paths.get("uploads/profiles").toAbsolutePath().normalize();

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory for profile pictures.", ex);
        }
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public String saveProfileImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store profile picture.", ex);
        }
    }

    public void updateProfilePicture(Long studentId, String fileName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setProfilePicture(fileName);
        studentRepository.save(student);
    }

    public void removeProfilePicture(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setProfilePicture(null); // Clear the field
        studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByUniversityEmail(email).orElse(null); 
    }

    public Student login(String email, String password) {
        Optional<Student> studentOpt = studentRepository.findByUniversityEmail(email);
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
                if(newData.getProfilePicture() != null) {
                    student.setProfilePicture(newData.getProfilePicture());
                }
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