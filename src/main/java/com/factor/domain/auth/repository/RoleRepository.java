package com.factor.domain.auth.repository;

import com.factor.domain.auth.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(String id);

    List<Role> findAll();

    Role save(Role role);

    void deleteById(String id);
}
