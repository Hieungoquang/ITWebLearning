package com.ngoquanghieu.thvp.dto;

import com.ngoquanghieu.thvp.entity.Course.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Status status;
    private String instructorName;
    private Long instructorId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String rejectReason;
    private int enrollmentCount;
}