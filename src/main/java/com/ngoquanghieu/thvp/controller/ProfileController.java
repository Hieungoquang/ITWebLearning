package com.ngoquanghieu.thvp.controller;

import org.springframework.ui.Model;
import com.ngoquanghieu.thvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public String view(Model model, Authentication auth) {
        model.addAttribute("user", userService.getCurrentUser(auth));
        return "profile-view";
    }

    @GetMapping("/edit")
    public String edit(Model model, Authentication auth) {
        model.addAttribute("user", userService.getCurrentUser(auth));
        return "profile-edit";
    }

    @PostMapping("/update")
    public String update(@RequestParam String fullName,
                         @RequestParam String email,
                         @RequestParam(required = false) String bio,
                         @RequestParam(required = false) MultipartFile avatar,
                         Authentication auth,
                         RedirectAttributes ra) {
        try {
            var user = userService.getCurrentUser(auth);
            userService.updateProfile(user, fullName, email, bio, avatar);
            ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
