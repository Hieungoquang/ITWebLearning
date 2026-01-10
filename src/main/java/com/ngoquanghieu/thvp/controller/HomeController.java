package com.ngoquanghieu.thvp.controller;

import org.springframework.ui.Model;
import com.ngoquanghieu.thvp.service.EnrollmentService;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;

    @GetMapping({"/","/home"})
    public String home(Model model, Authentication auth) {
        var user = userService.getCurrentUser(auth);
        var enrollments = enrollmentService.getEnrollmentsByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", enrollments);
        model.addAttribute("overallProgress", enrollmentService.calculateOverallProgress(user));
        return "home";
    }
}
