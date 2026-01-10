package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Enrollment;
import com.ngoquanghieu.thvp.entity.EnrollmentId;
import com.ngoquanghieu.thvp.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    List<Enrollment> findByUserId(Long userId);  // ← Long
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);  // ← Long
    boolean existsById(EnrollmentId id);  // ← Dùng EnrollmentId
}