package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lessons")
@Data
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    private ContentType contentType = ContentType.VIDEO;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    public enum ContentType {
        VIDEO, TEXT, FILE, QUIZ
    }
}
