package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.dto.CourseDTO;
import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EmailService emailService;

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Page<Course> getCoursesByStatus(Course.Status status, Pageable pageable) {
        return courseRepository.findByStatus(status, pageable);
    }

    public Page<Course> getPendingCourses(Pageable pageable) {
        return courseRepository.findByStatus(Course.Status.PENDING, pageable);
    }

    public Page<Course> getPublishedCourses(Pageable pageable) {
        return courseRepository.findByStatus(Course.Status.PUBLISHED, pageable);
    }

    public Page<Course> getRejectedCourses(Pageable pageable) {
        return courseRepository.findByStatus(Course.Status.REJECTED, pageable);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khóa học không tồn tại với ID: " + id));
    }

    public long countAllCourses() {
        return courseRepository.count();
    }

    public long countByStatus(Course.Status status) {
        return courseRepository.countByStatus(status);
    }

    public List<Course> getRecentCourses(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        return courseRepository.findAll(pageable).getContent();
    }

    public List<Course> getTopCoursesByEnrollmentCount(int limit) {
        return courseRepository.findTopCoursesByEnrollmentCount(limit);
    }

    // ====================== TẠO - CẬP NHẬT - XÓA ======================

    @Transactional
    public Course createCourse(Course course, User instructor) {
        if (instructor == null || instructor.getRole() != User.Role.TEACHER) {
            throw new IllegalArgumentException("Chỉ giáo viên mới có thể tạo khóa học!");
        }
        course.setInstructor(instructor);
        course.setStatus(Course.Status.DRAFT);
        course.setCreatedDate(LocalDateTime.now());
        course.setUpdatedDate(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course updatedCourse, User currentUser) {
        Course course = getCourseById(id);

        if (!course.getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Bạn không có quyền sửa khóa học này!");
        }

        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setThumbnailUrl(updatedCourse.getThumbnailUrl());

        course.setUpdatedDate(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id, User currentUser) {
        Course course = getCourseById(id);

        if (!course.getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Bạn không có quyền xóa khóa học này!");
        }

        courseRepository.delete(course);
    }

    // ====================== QUẢN TRỊ: GỬI DUYỆT - DUYỆT - TỪ CHỐI ======================

    @Transactional
    public void submitForReview(Long courseId, User instructor) {
        Course course = getCourseById(courseId);

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new IllegalArgumentException("Chỉ người tạo mới có thể gửi duyệt!");
        }

        if (course.getStatus() != Course.Status.DRAFT) {
            throw new IllegalStateException("Khóa học phải ở trạng thái DRAFT để gửi duyệt!");
        }

        course.setStatus(Course.Status.PENDING);
        course.setUpdatedDate(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Transactional
    public void approveCourse(Long id) {
        Course course = getCourseById(id);

        if (course.getStatus() != Course.Status.PENDING) {
            throw new IllegalStateException("Chỉ có thể duyệt khóa học đang ở trạng thái PENDING!");
        }

        course.setStatus(Course.Status.PUBLISHED);
        course.setUpdatedDate(LocalDateTime.now());
        courseRepository.save(course);

        if (course.getInstructor() != null && course.getInstructor().getEmail() != null) {
            emailService.sendCourseApprovedEmail(course.getInstructor().getEmail(), course.getTitle());
        }
    }

    @Transactional
    public void rejectCourse(Long id, String rejectReason) {
        Course course = getCourseById(id);

        if (course.getStatus() != Course.Status.PENDING) {
            throw new IllegalStateException("Chỉ có thể từ chối khóa học đang ở trạng thái PENDING!");
        }

        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng cung cấp lý do từ chối!");
        }

        course.setStatus(Course.Status.REJECTED);
        course.setRejectReason(rejectReason.trim());
        course.setUpdatedDate(LocalDateTime.now());
        courseRepository.save(course);

        if (course.getInstructor() != null && course.getInstructor().getEmail() != null) {
            emailService.sendCourseRejectedEmail(course.getInstructor().getEmail(), course.getTitle(), rejectReason);
        }
    }

    public List<CourseDTO> getRecentCoursesDTO(int limit) {
        return getRecentCourses(limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .status(course.getStatus())
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : "Chưa có giảng viên")
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .createdDate(course.getCreatedDate())
                .updatedDate(course.getUpdatedDate())
                .rejectReason(course.getRejectReason())
                .enrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0)
                .build();
    }

}