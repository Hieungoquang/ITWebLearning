package com.ngoquanghieu.thvp.controller;

import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    //Hiên thị form đăng nhập
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    //Hiển thị form đăng kí
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           String phoneNumber,
                           BindingResult result,
                           String confirmPassword,
                           RedirectAttributes ra) {
        // Validate thủ công
        if (result.hasErrors()) {
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/register";
        }

        try {
            userService.register(user);
            ra.addFlashAttribute("success", "Đăng ký tài khoản thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
