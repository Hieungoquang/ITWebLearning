package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.Enrollment;
import com.ngoquanghieu.thvp.entity.EnrollmentId;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public Enrollment enroll(User student, Course course) {
        if (student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("Chỉ học viên mới có thể đăng ký khóa học!");
        }
        EnrollmentId id = new EnrollmentId(student.getId(), course.getId());
        if (enrollmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Bạn đã đăng ký khóa học này rồi!");
        }

        Enrollment enrollment = Enrollment.builder()
                .userId(student.getId())
                .courseId(course.getId())
                .user(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional
    public void updateProgress(Long userId, Long courseId, int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Tiến độ phải từ 0 đến 100!");
        }

        Optional<Enrollment> optEnrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        Enrollment enrollment = optEnrollment.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đăng ký!"));

        enrollment.setProgressPercentage(progress);
        if (progress >= 100) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
    }

    public long countAllEnrollments() {
        return enrollmentRepository.count();
    }

    public int calculateOverallProgress(User user) {
        List<Enrollment> enrollments = getEnrollmentsByUser(user.getId());
        if (enrollments.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Enrollment e : enrollments) {
            sum += e.getProgressPercentage();
        }
        return sum / enrollments.size();
    }

    public boolean isEnrolled(User user, Course course) {
        if (user == null || course == null) {
            return false;
        }
        return enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId()).isPresent();
    }
}