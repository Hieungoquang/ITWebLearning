package com.ngoquanghieu.thvp.repository;

import com.ngoquanghieu.thvp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail (String email);
    List<User> findByRole(User.Role role);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
