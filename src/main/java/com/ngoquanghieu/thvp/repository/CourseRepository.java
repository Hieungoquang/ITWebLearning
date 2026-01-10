package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    List<Course> findByInstructorId(Long instructorId);
}
