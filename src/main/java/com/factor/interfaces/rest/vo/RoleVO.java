package com.factor.interfaces.rest.vo;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.RoleScope;
import com.factor.domain.auth.UserType;

import java.util.List;

public record RoleVO(
        String id,
        String code,
        String name,
        UserType userType,
        RoleScope scope,
        List<PermissionCode> permissions,
        boolean builtIn
) {
    public static RoleVO from(Role role) {
        return new RoleVO(role.id(), role.code(), role.name(), role.userType(), role.scope(), role.permissions(), role.builtIn());
    }
}
