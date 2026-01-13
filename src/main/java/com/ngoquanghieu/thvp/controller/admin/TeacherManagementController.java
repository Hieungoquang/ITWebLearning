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
@RequestMapping("/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class TeacherManagementController {

    private final UserService userService;

    @GetMapping
    public String listTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> teachers = userService.getUsersByRole(User.Role.TEACHER, pageable);

        model.addAttribute("teachers", teachers);
        model.addAttribute("keyword", keyword);
        return "admin/teachers/list";
    }

    @GetMapping("/new")
    public String newTeacherForm(Model model) {
        model.addAttribute("teacher", new User());
        return "admin/teachers/form";
    }

    @PostMapping
    public String createTeacher(User teacher, RedirectAttributes ra) {
        try {
            teacher.setRole(User.Role.TEACHER);
            userService.register(teacher);  // sử dụng register để encode password
            ra.addFlashAttribute("success", "Tạo giáo viên thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/teachers";
    }
}