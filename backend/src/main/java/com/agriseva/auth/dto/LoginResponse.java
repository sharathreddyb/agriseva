package com.agriseva.auth.dto;

import com.agriseva.user.model.RoleType;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresInMilliseconds;

    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Set<RoleType> roles;
}