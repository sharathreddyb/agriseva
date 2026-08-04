package com.agriseva.user.service;

import com.agriseva.user.dto.RegisterUserRequest;
import com.agriseva.user.dto.UserResponse;
import com.agriseva.user.exception.EmailAlreadyExistsException;
import com.agriseva.user.exception.PhoneNumberAlreadyExistsException;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.RoleRepository;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                roleRepository,
                passwordEncoder
        );
    }

    @Test
    void registerUserShouldCreateFarmerAccount() {
        RegisterUserRequest request = createRequest();

        Role farmerRole = new Role(RoleType.FARMER);
        farmerRole.setId(1L);

        when(userRepository.existsByEmailIgnoreCase("sharath.test@example.com"))
                .thenReturn(false);
        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(false);
        when(roleRepository.findByName(RoleType.FARMER))
                .thenReturn(Optional.of(farmerRole));
        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        UserResponse response = userService.registerUser(request);

        assertEquals(1L, response.getId());
        assertEquals("Sharath Reddy", response.getFullName());
        assertEquals("sharath.test@example.com", response.getEmail());
        assertEquals("9876543210", response.getPhoneNumber());
        assertTrue(response.isActive());
        assertTrue(response.getRoles().contains(RoleType.FARMER));

        verify(passwordEncoder).encode("Password@123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldRejectDuplicateEmail() {
        RegisterUserRequest request = createRequest();

        when(userRepository.existsByEmailIgnoreCase("sharath.test@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, passwordEncoder);
    }

    @Test
    void registerUserShouldRejectDuplicatePhoneNumber() {
        RegisterUserRequest request = createRequest();

        when(userRepository.existsByEmailIgnoreCase("sharath.test@example.com"))
                .thenReturn(false);
        when(userRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(true);

        assertThrows(
                PhoneNumberAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, passwordEncoder);
    }

    private RegisterUserRequest createRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFullName("Sharath Reddy");
        request.setEmail("Sharath.Test@Example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("Password@123");
        request.setAddressLine("Near Main Road");
        request.setVillage("Chinnagundavelly");
        request.setDistrict("Siddipet");
        request.setState("Telangana");
        request.setPostalCode("502103");

        return request;
    }
}