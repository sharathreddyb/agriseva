package com.agriseva.auth.service;

import com.agriseva.auth.dto.LoginRequest;
import com.agriseva.auth.dto.LoginResponse;
import com.agriseva.auth.exception.InvalidCredentialsException;
import com.agriseva.auth.security.JwtService;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                authenticationManager,
                userRepository,
                jwtService
        );
    }

    @Test
    void loginShouldReturnTokenAndUserDetails() {
        LoginRequest request = createRequest(
                "SHARATH.TEST@EXAMPLE.COM"
        );

        Role farmerRole = new Role(RoleType.FARMER);

        User user = new User();
        user.setId(1L);
        user.setFullName("Sharath Reddy");
        user.setEmail("sharath.test@example.com");
        user.setPhoneNumber("9876543210");
        user.addRole(farmerRole);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername())
                .thenReturn("sharath.test@example.com");
        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(user));
        when(jwtService.generateToken(userDetails))
                .thenReturn("sample-jwt-token");
        when(jwtService.getExpirationMilliseconds())
                .thenReturn(3600000L);

        LoginResponse response = authService.login(request);

        assertEquals("sample-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresInMilliseconds());
        assertEquals(1L, response.getUserId());
        assertEquals("Sharath Reddy", response.getFullName());
        assertTrue(response.getRoles().contains(RoleType.FARMER));

        verify(authenticationManager).authenticate(
                argThat(authenticationToken ->
                        authenticationToken
                                instanceof UsernamePasswordAuthenticationToken
                                && authenticationToken.getName()
                                .equals("sharath.test@example.com")
                )
        );
    }

    @Test
    void loginShouldAcceptPhoneNumber() {
        LoginRequest request = createRequest("9876543210");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException(
                        "Invalid credentials"
                ));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager).authenticate(
                argThat(authenticationToken ->
                        authenticationToken.getName()
                                .equals("9876543210")
                )
        );
    }

    @Test
    void loginShouldRejectInvalidCredentials() {
        LoginRequest request = createRequest(
                "sharath.test@example.com"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException(
                        "Invalid credentials"
                ));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(userRepository, jwtService);
    }

    private LoginRequest createRequest(String loginId) {
        LoginRequest request = new LoginRequest();
        request.setLoginId(loginId);
        request.setPassword("Password@123");
        return request;
    }
}