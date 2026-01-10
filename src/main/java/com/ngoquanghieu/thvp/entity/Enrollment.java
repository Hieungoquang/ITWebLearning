package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serializable;

@Entity
@Table(name = "enrollments")
@Data
@IdClass(EnrollmentId.class) // Composite key
public class Enrollment {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @Column(name = "enrolled_date")
    private LocalDateTime enrolledDate = LocalDateTime.now();

    @Column(name = "progress_percentage")
    private int progressPercentage = 0;

    private boolean completed = false;

    @Column(name = "last_accessed_lesson_id")
    private Long lastAccessedLessonId;
}




