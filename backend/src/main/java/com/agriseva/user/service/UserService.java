package com.agriseva.user.service;

import com.agriseva.user.dto.RegisterUserRequest;
import com.agriseva.user.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(RegisterUserRequest request);
}