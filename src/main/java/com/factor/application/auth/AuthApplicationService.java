package com.factor.application.auth;

import com.factor.domain.auth.AuthSession;

public interface AuthApplicationService {
    AuthSession login(String username, String password, String userType);
}
