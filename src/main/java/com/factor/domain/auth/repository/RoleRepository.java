package com.factor.domain.auth.repository;

import com.factor.domain.auth.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(String id);
}
