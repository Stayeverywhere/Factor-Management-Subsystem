package com.factor.domain.auth;

import java.util.List;

public record AuthSession(
        String token,
        Account account,
        Role role,
        List<MenuItem> menus
) {
}
