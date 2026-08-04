package com.agriseva.user.controller;

import com.agriseva.common.exception.GlobalExceptionHandler;
import com.agriseva.user.dto.UserResponse;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerUserShouldReturnCreated() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .fullName("Sharath Reddy")
                .email("sharath.test@example.com")
                .phoneNumber("9876543210")
                .active(true)
                .roles(Set.of(RoleType.FARMER))
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Sharath Reddy",
                                  "email": "sharath.test@example.com",
                                  "phoneNumber": "9876543210",
                                  "password": "Password@123",
                                  "district": "Siddipet",
                                  "state": "Telangana",
                                  "postalCode": "502103"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("sharath.test@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("FARMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registerUserShouldReturnBadRequestForInvalidInput() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "",
                                  "email": "invalid-email",
                                  "phoneNumber": "12345",
                                  "password": "short",
                                  "postalCode": "000000"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.phoneNumber").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.postalCode").exists());
    }
}