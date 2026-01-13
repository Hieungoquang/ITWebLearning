package com.ngoquanghieu.thvp.controller;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.service.CourseService;
import com.ngoquanghieu.thvp.service.EnrollmentService;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping("/courses")
    public String listCourses(Model model, Authentication auth,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "12") int size) {

        User user = null;
        if (auth != null && auth.isAuthenticated()) {
            user = userService.getCurrentUser(auth);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = courseService.getPublishedCourses(pageable); // chỉ lấy khóa đã publish

        model.addAttribute("user", user);
        model.addAttribute("courses", coursesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursesPage.getTotalPages());

        return "course-view";
    }

    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model, Authentication auth) {
        Course course = courseService.getCourseById(id);

        User user = null;
        boolean isEnrolled = false;
        if (auth != null && auth.isAuthenticated()) {
            user = userService.getCurrentUser(auth);
            isEnrolled = enrollmentService.isEnrolled(user, course); // cần thêm method này vào EnrollmentService
        }

        model.addAttribute("course", course);
        model.addAttribute("user", user);
        model.addAttribute("isEnrolled", isEnrolled);

        return "course-detail";
    }

    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId,
                         Authentication auth,
                         RedirectAttributes ra) {

        if (auth == null || !auth.isAuthenticated()) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập để tham gia khóa học!");
            return "redirect:/login";
        }

        try {
            User user = userService.getCurrentUser(auth);
            Course course = courseService.getCourseById(courseId);

            if (course.getStatus() != Course.Status.PUBLISHED) {
                ra.addFlashAttribute("error", "Khóa học chưa được xuất bản hoặc không tồn tại!");
                return "redirect:/course/" + courseId;
            }

            enrollmentService.enroll(user, course);

            ra.addFlashAttribute("success", "Tham gia khóa học thành công!");
            return "redirect:/course/" + courseId; // quay lại trang chi tiết khóa học

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/course/" + courseId;
        }
    }
}