package com.ngoquanghieu.thvp.controller.admin;

import com.ngoquanghieu.thvp.entity.Course;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.service.CourseService;
import com.ngoquanghieu.thvp.service.EnrollmentService;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countAllUsers());
        model.addAttribute("totalStudents", userService.countByRole(User.Role.STUDENT));
        model.addAttribute("totalTeachers", userService.countByRole(User.Role.TEACHER));
        model.addAttribute("totalCourses", courseService.countAllCourses());
        model.addAttribute("pendingCourses", courseService.countByStatus(Course.Status.PENDING));
        model.addAttribute("totalEnrollments", enrollmentService.countAllEnrollments());
        model.addAttribute("recentCourses", courseService.getRecentCourses(5));
        model.addAttribute("recentUsers", userService.getRecentUsers(5));
        return "admin/dashboard";
    }
}