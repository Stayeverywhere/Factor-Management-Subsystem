package com.factor.infrastructure.persistence;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryRoleRepository implements RoleRepository {

    private final List<Role> roles = List.of(
            new Role("r1", "SYSTEM_ADMIN", "系统超级管理员", UserType.SYSTEM_ADMIN, List.of(PermissionCode.values()), true),
            new Role("r2", "CUSTOMER_ADMIN", "客户管理员", UserType.CUSTOMER, List.of(
                    PermissionCode.FACTOR_READ,
                    PermissionCode.FACTOR_TREE_MANAGE,
                    PermissionCode.DERIVED_FACTOR_MANAGE,
                    PermissionCode.STYLE_FACTOR_MANAGE
            ), true)
    );

    @Override
    public Optional<Role> findById(String id) {
        return roles.stream().filter(role -> role.id().equals(id)).findFirst();
    }
}
