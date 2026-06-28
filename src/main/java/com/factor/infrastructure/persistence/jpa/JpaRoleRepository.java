package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.RoleScope;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaRoleRepository implements RoleRepository {

    private final RoleJpaRepository jpa;

    public JpaRoleRepository(RoleJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Role> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = toEntity(role);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        return toDomain(jpa.save(entity));
    }

    @Override
    public void deleteById(String id) {
        jpa.findById(id).ifPresent(e -> {
            if (!e.isBuiltIn()) jpa.deleteById(id);
        });
    }

    private Role toDomain(RoleEntity e) {
        List<PermissionCode> perms = e.getPermissions() == null || e.getPermissions().isBlank()
                ? List.of()
                : Arrays.stream(e.getPermissions().split(","))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .map(PermissionCode::valueOf).toList();
        return new Role(
                e.getId(), e.getCode(), e.getName(),
                UserType.valueOf(e.getUserType()),
                RoleScope.valueOf(e.getScope()),
                perms, e.isBuiltIn()
        );
    }

    private RoleEntity toEntity(Role r) {
        RoleEntity e = new RoleEntity();
        e.setId(r.id()); e.setCode(r.code()); e.setName(r.name());
        e.setUserType(r.userType().name()); e.setScope(r.scope().name());
        e.setPermissions(r.permissions().stream().map(PermissionCode::name).collect(Collectors.joining(",")));
        e.setBuiltIn(r.builtIn());
        return e;
    }
}
