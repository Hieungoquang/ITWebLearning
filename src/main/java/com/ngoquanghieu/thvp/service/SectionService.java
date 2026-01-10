package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.Section;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseService courseService;

    public Section createModule(Section module, Long courseId, User currentUser) {
        Course course = courseService.getCourseById(courseId);

        if (!course.getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền thêm module vào khóa học này!");
        }

        module.setCourse(course);

        if (module.getOrderIndex() == 0) {
            int maxOrder = sectionRepository.findMaxOrderByCourseId(courseId);
            module.setOrderIndex(maxOrder + 1);
        }

        return sectionRepository.save(module);
    }

    public Section updateModule(Long moduleId, Section updatedModule, User currentUser) {
        Section module = getModuleById(moduleId);
        Course course = module.getCourse();

        if (!course.getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền sửa module!");
        }

        module.setTitle(updatedModule.getTitle());
        module.setDescription(updatedModule.getDescription());
        module.setOrderIndex(updatedModule.getOrderIndex());

        return sectionRepository.save(module);
    }

    public void deleteModule(Long moduleId, User currentUser) {
        Section module = getModuleById(moduleId);
        Course course = module.getCourse();

        if (!course.getInstructor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền xóa module!");
        }

        sectionRepository.delete(module);
    }

    public Section getModuleById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module không tồn tại!"));
    }

    public List<Section> getModulesByCourseId(Long courseId) {
        return sectionRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }
}