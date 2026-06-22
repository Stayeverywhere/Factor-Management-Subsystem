package com.factor.application.auth;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;

import java.util.List;
import java.util.Optional;

public interface RoleApplicationService {
    List<Role> listRoles();

    Optional<Role> getRole(String id);

    Role createRole(Role role);

    Role updateRole(String id, Role role);

    void deleteRole(String id);

    List<PermissionCode> listTemplatePermissions(String userType);
}
