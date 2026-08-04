package com.agriseva.user.exception;

import com.agriseva.user.model.RoleType;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(RoleType roleType) {
        super("Required role is not configured: " + roleType);
    }
}