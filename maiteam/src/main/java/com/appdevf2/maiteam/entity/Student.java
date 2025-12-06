package com.appdevf2.maiteam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(unique = true)
    private String studentNumber;

    @Column(unique = true)
    private String universityEmail;

    private String firstName;
    private String lastName;
    private String passwordHash;
    private String phoneNumber;
    private String profilePicture;
    private Boolean isVerified;
    private String accountStatus;
}
    