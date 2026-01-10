package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String AVATAR_DIR ="";

    public User getCurrentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(()-> new RuntimeException("User nay khong ton tai"));
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if(user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại này đã được sử dung");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setEnabled(true);
        user.setJoinDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void updateProfile(User user, String fullName, String email, String bio, MultipartFile avatar) throws IOException {
        user.setFullName(fullName);
        user.setEmail(email);
        user.setBio(bio == null ? " ":bio);

        if (avatar != null && !avatar.isEmpty()) {
            Path uploadPath = Paths.get(AVATAR_DIR);
            Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, avatar.getBytes());

            user.setAvatarUrl("/images/avatars/" + fileName);
        }
        userRepository.save(user);
    }
}
