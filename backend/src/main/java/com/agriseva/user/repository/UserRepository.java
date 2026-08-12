package com.agriseva.user.repository;

import com.agriseva.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailIgnoreCaseOrPhoneNumber(
            String email,
            String phoneNumber
    );    

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}