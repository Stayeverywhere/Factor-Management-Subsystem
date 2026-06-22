package com.factor.domain.auth;

import java.util.List;

public record Role(
        String id,
        String code,
        String name,
        UserType userType,
        List<PermissionCode> permissions,
        boolean builtIn
) {
}
