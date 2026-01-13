package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.DRAFT;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    // Thêm các trường hữu ích khác (tùy chọn)
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(length = 50)
    private String level;  // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(length = 100)
    private String language;

    public enum Status {
        DRAFT, PENDING, PUBLISHED, REJECTED
    }

    // Optional: phương thức tiện ích
    public boolean isPublished() {
        return this.status == Status.PUBLISHED;
    }

    public boolean isPending() {
        return this.status == Status.PENDING;
    }

    public boolean isRejected() {
        return this.status == Status.REJECTED;
    }

    // Để tránh NullPointerException khi add module/enrollment
    public void addModule(Section module) {
        if (modules == null) {
            modules = new ArrayList<>();
        }
        modules.add(module);
        module.setCourse(this);
    }

    public void removeModule(Section module) {
        if (modules != null) {
            modules.remove(module);
            module.setCourse(null);
        }
    }
}