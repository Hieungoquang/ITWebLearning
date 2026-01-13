package com.ngoquanghieu.thvp.controller.admin;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.service.CourseService;
import com.ngoquanghieu.thvp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CourseApprovalController {

    private final CourseService courseService;
    private final EmailService emailService;

    @GetMapping("/pending")
    public String pendingCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Course> pendingCourses = courseService.getPendingCourses(pageable);

        model.addAttribute("courses", pendingCourses);
        return "admin/courses/pending";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        try {
            courseService.approveCourse(id);
            Course course = courseService.getCourseById(id);
            emailService.sendCourseApprovedEmail(course.getInstructor().getEmail(), course.getTitle());
            ra.addFlashAttribute("success", "Khóa học đã được duyệt!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/courses/pending";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam("rejectReason") String reason, RedirectAttributes ra) {
        try {
            courseService.rejectCourse(id, reason);
            Course course = courseService.getCourseById(id);
            emailService.sendCourseRejectedEmail(course.getInstructor().getEmail(), course.getTitle(), reason);
            ra.addFlashAttribute("success", "Khóa học đã bị từ chối!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/courses/pending";
    }
}