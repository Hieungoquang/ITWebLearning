package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now();

    private boolean enabled = true;

    // Quan hệ: Instructor tạo courses
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private List<Course> taughtCourses;

    // Quan hệ: Student tham gia courses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;

    public enum Role {
        STUDENT, INSTRUCTOR, ADMIN
    }
}
