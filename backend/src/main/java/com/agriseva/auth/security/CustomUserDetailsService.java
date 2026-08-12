package com.agriseva.auth.security;

import com.agriseva.user.model.Role;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {

        String normalizedLoginId = loginId.trim().toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCaseOrPhoneNumber(
                        normalizedLoginId,
                        normalizedLoginId
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No account found with the provided email or phone number"
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isActive())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .map(role -> new SimpleGrantedAuthority(
                                        "ROLE_" + role.name()
                                ))
                                .toList()
                )
                .build();
    }
}