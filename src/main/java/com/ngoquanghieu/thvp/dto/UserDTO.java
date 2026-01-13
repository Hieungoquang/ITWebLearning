package com.ngoquanghieu.thvp.dto;

import com.ngoquanghieu.thvp.entity.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Role role;
    private boolean enabled;
    private LocalDateTime joinDate;
    private LocalDateTime updatedAt;
}