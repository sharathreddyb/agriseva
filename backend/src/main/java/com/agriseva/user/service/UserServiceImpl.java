package com.agriseva.user.service;

import com.agriseva.user.dto.RegisterUserRequest;
import com.agriseva.user.dto.UserResponse;
import com.agriseva.user.exception.EmailAlreadyExistsException;
import com.agriseva.user.exception.PhoneNumberAlreadyExistsException;
import com.agriseva.user.exception.RoleNotFoundException;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.RoleRepository;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String normalizedPhoneNumber = request.getPhoneNumber().trim();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw new PhoneNumberAlreadyExistsException(normalizedPhoneNumber);
        }

        Role farmerRole = roleRepository.findByName(RoleType.FARMER)
                .orElseThrow(() -> new RoleNotFoundException(RoleType.FARMER));

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(normalizedPhoneNumber);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddressLine(clean(request.getAddressLine()));
        user.setVillage(clean(request.getVillage()));
        user.setDistrict(clean(request.getDistrict()));
        user.setState(clean(request.getState()));
        user.setPostalCode(clean(request.getPostalCode()));
        user.setActive(true);
        user.addRole(farmerRole);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {
        Set<RoleType> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .addressLine(user.getAddressLine())
                .village(user.getVillage())
                .district(user.getDistrict())
                .state(user.getState())
                .postalCode(user.getPostalCode())
                .active(user.isActive())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        String cleanedValue = value.trim();
        return cleanedValue.isEmpty() ? null : cleanedValue;
    }
}