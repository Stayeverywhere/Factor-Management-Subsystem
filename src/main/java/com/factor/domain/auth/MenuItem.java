package com.factor.domain.auth;

import java.util.List;

public record MenuItem(
        String id,
        String parentId,
        String name,
        String path,
        String component,
        String icon,
        PermissionCode requiredPermission,
        List<MenuItem> children
) {
}
