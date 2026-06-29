package com.factor.application.auth;

import com.factor.common.exception.BusinessException;
import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleApplicationServiceImpl implements RoleApplicationService {

    private final RoleRepository roleRepository;

    public RoleApplicationServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRole(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(String id, Role role) {
        Role existing = roleRepository.findById(id).orElseThrow(() -> new BusinessException("角色不存在"));
        if (existing.builtIn()) {
            return roleRepository.save(new Role(existing.id(), role.code(), role.name(), role.userType(), role.scope(), role.permissions(), true));
        }
        return roleRepository.save(new Role(existing.id(), role.code(), role.name(), role.userType(), role.scope(), role.permissions(), role.builtIn()));
    }

    @Override
    public void deleteRole(String id) {
        Role existing = roleRepository.findById(id).orElseThrow(() -> new BusinessException("角色不存在"));
        if (existing.builtIn()) {
            throw new BusinessException("内置角色不允许删除");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public List<PermissionCode> listTemplatePermissions(String userType) {
        return List.copyOf(RoleTemplateRegistry.defaultPermissions(UserType.valueOf(userType)));
    }
}
