package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại!"));
    }

    public Course createCourse(Course course, User instructor) {
        course.setInstructor(instructor);
        course.setStatus(Course.Status.DRAFT);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updatedCourse, User instructor) {
        Course course = getCourseById(id);
        if (!course.getInstructor().getId().equals(instructor.getId()) && instructor.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền sửa khóa học này!");
        }
        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setThumbnailUrl(updatedCourse.getThumbnailUrl());
        return courseRepository.save(course);
    }

    public void approveCourse(Long id) {
        Course course = getCourseById(id);
        course.setStatus(Course.Status.PUBLISHED);
        courseRepository.save(course);
    }

    public void deleteCourse(Long id, User user) {
        Course course = getCourseById(id);
        if (!course.getInstructor().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền xóa!");
        }
        courseRepository.delete(course);
    }
}
