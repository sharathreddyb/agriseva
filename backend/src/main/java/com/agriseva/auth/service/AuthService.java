package com.agriseva.auth.service;

import com.agriseva.auth.dto.LoginRequest;
import com.agriseva.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}