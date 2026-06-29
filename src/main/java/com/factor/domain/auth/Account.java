package com.factor.domain.auth;

public record Account(
        String id,
        String username,
        String passwordHash,
        String displayName,
        UserType userType,
        String roleId,
        String tenantId,
        boolean enabled
) {
}
