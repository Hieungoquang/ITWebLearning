package com.ngoquanghieu.thvp.controller;

import com.ngoquanghieu.thvp.entity.Enrollment;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.service.EnrollmentService;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProgressController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/progress")
    public String showProgress(Model model, Authentication auth){
        User user = userService.getCurrentUser(auth);
        List<Enrollment> enrolledCourses = enrollmentService.getEnrollmentsByUser(user.getId());

        int overallProgress = enrollmentService.calculateOverallProgress(user);

        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("overallProgress", overallProgress);

        model.addAttribute("totalHours", 120);
        model.addAttribute("completedCourses", 2);

        return "progress";
    }
}
