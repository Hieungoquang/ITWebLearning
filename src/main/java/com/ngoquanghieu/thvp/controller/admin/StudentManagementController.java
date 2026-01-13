package com.ngoquanghieu.thvp.controller.admin;

import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/students")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StudentManagementController {

    private final UserService userService;

    @GetMapping
    public String listStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> students = userService.getUsersByRole(User.Role.STUDENT, pageable);

        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        return "admin/students/list";
    }

    @PostMapping("/{id}/toggle-enabled")
    public String toggleEnabled(@RequestParam Long id, RedirectAttributes ra) {
        try {
            userService.toggleUserEnabled(id);
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/students";
    }
}