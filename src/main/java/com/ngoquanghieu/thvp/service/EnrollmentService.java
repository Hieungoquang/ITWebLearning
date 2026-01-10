package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Enrollment;
import com.ngoquanghieu.thvp.entity.EnrollmentId;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.EnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public void enroll(User user, Long courseId){
        EnrollmentId id = new EnrollmentId(user.getId(), courseId);
        if (enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Bạn đã tham gia khóa học này rồi!");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(user.getId());
        enrollment.setCourseId(courseId);
        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByUser(User user) {
        return enrollmentRepository.findByUserId(user.getId());
    }

    public void updateProgress(User user, long courseId, int progress){
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Chưa tham gia khóa học!"));
        enrollment.setProgressPercentage(progress);
        if (progress >= 100) enrollment.setCompleted(true);
        enrollmentRepository.save(enrollment);
    }
    public int calculateOverallProgress(User user) {
        List<Enrollment> enrollments = getEnrollmentsByUser(user);  // ← Dùng method mới
        if (enrollments.isEmpty()) return 0;
        return (int) enrollments.stream()
                .mapToInt(Enrollment::getProgressPercentage)
                .average()
                .orElse(0);
    }
}
