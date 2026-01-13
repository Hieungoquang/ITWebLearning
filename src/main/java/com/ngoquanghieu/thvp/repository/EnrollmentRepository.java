package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    long countByCourseId(Long courseId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByUserId(Long userId);

}