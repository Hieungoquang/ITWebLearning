package com.ngoquanghieu.thvp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;  // ADMIN, TEACHER, STUDENT

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === UserDetails implementation ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Enum Role (nên tách riêng ra file Role.java cho sạch)
    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    // Optional: thêm phương thức tiện ích
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isTeacher() {
        return this.role == Role.TEACHER;
    }

    public boolean isStudent() {
        return this.role == Role.STUDENT;
    }

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;          // cho check trùng số điện thoại

    @Column(columnDefinition = "TEXT")
    private String bio;                  // tiểu sử / giới thiệu

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;            // đường dẫn ảnh đại diện

    @Column(name = "join_date")
    private LocalDateTime joinDate;
}