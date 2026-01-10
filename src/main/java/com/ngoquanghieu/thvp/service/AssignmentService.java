package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Assignment;
import com.ngoquanghieu.thvp.entity.Lesson;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.AssignmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonService lessonService; // Đã có từ trước

    // Tạo bài tập mới cho một lesson
    public Assignment createAssignment(Assignment assignment, Long lessonId, User currentUser) {
        Lesson lesson = lessonService.getLessonById(lessonId);

        // Kiểm tra quyền: Chỉ instructor của khóa học hoặc admin
        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền tạo bài tập cho bài học này!");
        }

        assignment.setLesson(lesson);
        return assignmentRepository.save(assignment);
    }

    // Cập nhật bài tập
    public Assignment updateAssignment(Long assignmentId, Assignment updatedAssignment, User currentUser) {
        Assignment assignment = getAssignmentById(assignmentId);
        Lesson lesson = assignment.getLesson();

        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền sửa bài tập!");
        }

        assignment.setTitle(updatedAssignment.getTitle());
        assignment.setInstructions(updatedAssignment.getInstructions());
        assignment.setDueDate(updatedAssignment.getDueDate());
        assignment.setMaxScore(updatedAssignment.getMaxScore());

        return assignmentRepository.save(assignment);
    }

    // Xóa bài tập
    public void deleteAssignment(Long assignmentId, User currentUser) {
        Assignment assignment = getAssignmentById(assignmentId);
        Lesson lesson = assignment.getLesson();

        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền xóa bài tập!");
        }

        assignmentRepository.delete(assignment);
    }

    // Lấy bài tập theo ID
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài tập không tồn tại!"));
    }
}
