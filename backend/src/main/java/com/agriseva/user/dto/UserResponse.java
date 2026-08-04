package com.agriseva.user.dto;

import com.agriseva.user.model.RoleType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String addressLine;
    private String village;
    private String district;
    private String state;
    private String postalCode;
    private boolean active;
    private Set<RoleType> roles;
    private LocalDateTime createdAt;
}