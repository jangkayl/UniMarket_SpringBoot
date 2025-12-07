package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.service.StudentService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final Path fileStorageLocation = Paths.get("uploads/profiles").toAbsolutePath().normalize();

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Serve Profile Image
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Upload Profile Image
    @PostMapping(value = "/{id}/upload-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            String fileName = studentService.saveProfileImage(file);
            studentService.updateProfilePicture(id, fileName);
            return ResponseEntity.ok().body(Map.of("message", "Upload successful", "fileName", fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    // --- NEW: DELETE PROFILE IMAGE ---
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable Long id) {
        try {
            studentService.removeProfilePicture(id);
            return ResponseEntity.ok().body(Map.of("message", "Profile picture removed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove image");
        }
    }

    @PostMapping("/addStudent")
    public Student addStudent(@RequestBody Student student) {
        return studentService.save(student);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        Student student = studentService.login(email, password);

        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @GetMapping("/getAllStudents")
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }

    @GetMapping("/getStudentByEmail/{email}")
    public Student getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        // Assuming findAll filter or adding getById to service. 
        // For brevity, using stream on findAll or you should add findById in Service
        // Ideally add findById to Service. Here is a quick hack or assume it exists:
        return ResponseEntity.ok(studentService.findAll().stream().filter(s -> s.getStudentId().equals(id)).findFirst().orElse(null));
    }

    @PutMapping("/updateStudent/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/deleteStudent/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
    }
}