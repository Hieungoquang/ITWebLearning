package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByStatus(Course.Status status);

    List<Course> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c.id ORDER BY COUNT(e.id) DESC")
    List<Course> findTopCoursesByEnrollmentCount(@Param("limit") int limit);

    Page<Course> findByStatus(Course.Status status, Pageable pageable);
    long countByStatus(Course.Status status);
}
