package com.factor.infrastructure.persistence;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.RoleScope;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryRoleRepository implements RoleRepository {

    private final List<Role> roles = new ArrayList<>(List.of(
            new Role("r1", "SYSTEM_ADMIN", "系统超级管理员", UserType.SYSTEM_ADMIN, RoleScope.SYSTEM, List.of(PermissionCode.values()), true),
            new Role("r2", "TRADER", "交易员", UserType.TRADER, RoleScope.BUSINESS, List.of(
                    PermissionCode.TRADE_ORDER_VIEW,
                    PermissionCode.TRADE_ORDER_EXECUTE,
                    PermissionCode.TRADE_ORDER_REVIEW,
                    PermissionCode.PORTFOLIO_VIEW,
                    PermissionCode.DISCLOSURE_VIEW
            ), true),
            new Role("r3", "CUSTOMER", "客户", UserType.CUSTOMER, RoleScope.BUSINESS, List.of(
                    PermissionCode.PORTFOLIO_VIEW,
                    PermissionCode.AGREEMENT_VIEW,
                    PermissionCode.AGREEMENT_SIGN,
                    PermissionCode.REDEMPTION_REQUEST,
                    PermissionCode.DISCLOSURE_VIEW
            ), true)
    ));

    @Override
    public Optional<Role> findById(String id) {
        return roles.stream().filter(role -> role.id().equals(id)).findFirst();
    }

    @Override
    public List<Role> findAll() {
        return List.copyOf(roles);
    }

    @Override
    public Role save(Role role) {
        Role stored = role.id() == null ? new Role(UUID.randomUUID().toString(), role.code(), role.name(), role.userType(), role.scope(), role.permissions(), role.builtIn()) : role;
        roles.removeIf(existing -> existing.id().equals(stored.id()));
        roles.add(stored);
        return stored;
    }

    @Override
    public void deleteById(String id) {
        roles.removeIf(role -> role.id().equals(id) && !role.builtIn());
    }
}
