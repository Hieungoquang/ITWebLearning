package com.ngoquanghieu.thvp.service;

import com.ngoquanghieu.thvp.dto.UserDTO;
import com.ngoquanghieu.thvp.entity.User;
import com.ngoquanghieu.thvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Thư mục lưu avatar (cấu hình trong application.properties tốt hơn)
    private static final String AVATAR_DIR = "src/main/resources/static/images/avatars/";
    private static final String AVATAR_URL_PREFIX = "/images/avatars/";

    public User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Không tìm thấy người dùng đang đăng nhập!");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại: " + auth.getName()));
    }

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setEnabled(true);
        user.setJoinDate(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void updateProfile(User user, String fullName, String email, String bio, MultipartFile avatar) throws IOException {
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email mới đã được sử dụng!");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setBio(bio != null ? bio.trim() : "");

        if (avatar != null && !avatar.isEmpty()) {
            Path uploadPath = Paths.get(AVATAR_DIR);
            Files.createDirectories(uploadPath);

            String originalFilename = avatar.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "avatar");
            Path filePath = uploadPath.resolve(fileName);

            Files.write(filePath, avatar.getBytes());

            user.setAvatarUrl(AVATAR_URL_PREFIX + fileName);
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public long countAllUsers() {
        return userRepository.count();
    }

    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    public Page<User> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    public List<User> getRecentUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "joinDate"));
        return userRepository.findAll(pageable).getContent();
    }

    @Transactional
    public void toggleUserEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với ID: " + userId));
        user.setEnabled(!user.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Thêm nếu cần: tìm kiếm user
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(pattern, pattern, pageable);
    }

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .joinDate(user.getJoinDate())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}