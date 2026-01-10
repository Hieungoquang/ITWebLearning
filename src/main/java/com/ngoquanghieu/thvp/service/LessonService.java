package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Lesson;
import com.ngoquanghieu.thvp.entity.Section;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {
    private final LessonRepository lessonRepository;
    private final SectionService sectionService;

    // Tạo lesson mới
    public Lesson createLesson(Lesson lesson, Long moduleId, User currentUser) {
        Section module = sectionService.getModuleById(moduleId);

        // Kiểm tra quyền qua module → course → instructor
        if (!module.getCourse().getInstructor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền thêm bài học!");
        }

        lesson.setSection(module);
        if (lesson.getOrderIndex() == 0) {
            int maxOrder = lessonRepository.findMaxOrderByModuleId(moduleId);
            lesson.setOrderIndex(maxOrder + 1);
        }

        return lessonRepository.save(lesson);
    }

    // Cập nhật lesson
    public Lesson updateLesson(Long lessonId, Lesson updatedLesson, User currentUser) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền sửa bài học!");
        }

        lesson.setTitle(updatedLesson.getTitle());
        lesson.setContentType(updatedLesson.getContentType());
        lesson.setVideoUrl(updatedLesson.getVideoUrl());
        lesson.setContentText(updatedLesson.getContentText());
        lesson.setFileUrl(updatedLesson.getFileUrl());
        lesson.setDurationMinutes(updatedLesson.getDurationMinutes());
        lesson.setOrderIndex(updatedLesson.getOrderIndex());

        return lessonRepository.save(lesson);
    }

    // Xóa lesson
    public void deleteLesson(Long lessonId, User currentUser) {
        Lesson lesson = getLessonById(lessonId);

        if (!lesson.getSection().getCourse().getInstructor().getId().equals(currentUser.getId()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền xóa bài học!");
        }

        lessonRepository.delete(lesson);
    }

    // Lấy lesson theo ID
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Bài học không tồn tại!"));
    }

    // Lấy tất cả lesson của module
    public List<Lesson> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findBySectionIdOrderByOrderIndexAsc(moduleId);
    }
}
