package com.factor.domain.auth.repository;

import com.factor.domain.auth.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByUsername(String username);
}
