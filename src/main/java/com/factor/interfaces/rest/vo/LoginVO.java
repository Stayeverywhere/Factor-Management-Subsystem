package com.factor.interfaces.rest.vo;

import com.factor.domain.auth.AuthSession;
import com.factor.domain.auth.MenuItem;
import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.UserType;

import java.util.List;

public record LoginVO(
        String token,
        AccountVO account,
        RoleVO role,
        List<MenuVO> menus
) {
    public static LoginVO from(AuthSession session) {
        return new LoginVO(
                session.token(),
                AccountVO.from(session.account()),
                RoleVO.from(session.role()),
                session.menus().stream().map(MenuVO::from).toList()
        );
    }

    public record AccountVO(String id, String username, String displayName, UserType userType, String tenantId, boolean enabled) {
        public static AccountVO from(com.factor.domain.auth.Account account) {
            return new AccountVO(account.id(), account.username(), account.displayName(), account.userType(), account.tenantId(), account.enabled());
        }
    }

    public record RoleVO(String id, String code, String name, UserType userType, List<PermissionCode> permissions) {
        public static RoleVO from(Role role) {
            return new RoleVO(role.id(), role.code(), role.name(), role.userType(), role.permissions());
        }
    }

    public record MenuVO(String id, String name, String path, String component, String icon, PermissionCode requiredPermission, List<MenuVO> children) {
        public static MenuVO from(MenuItem item) {
            return new MenuVO(item.id(), item.name(), item.path(), item.component(), item.icon(), item.requiredPermission(), item.children().stream().map(MenuVO::from).toList());
        }
    }
}
