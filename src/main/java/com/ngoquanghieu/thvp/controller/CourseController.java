package com.ngoquanghieu.thvp.controller;

import org.springframework.ui.Model;
import com.ngoquanghieu.thvp.service.CourseService;
import com.ngoquanghieu.thvp.service.EnrollmentService;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "course-list"; // Bạn cần tạo HTML này
    }

    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "course-detail";
    }

    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId, Authentication auth, RedirectAttributes ra) {
        try {
            var user = userService.getCurrentUser(auth);
            enrollmentService.enroll(user, courseId);
            ra.addFlashAttribute("success", "Tham gia khóa học thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/home";
    }
}
