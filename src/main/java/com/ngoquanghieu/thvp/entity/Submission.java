package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "text_submission", columnDefinition = "TEXT")
    private String textSubmission;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    private Integer score;
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
