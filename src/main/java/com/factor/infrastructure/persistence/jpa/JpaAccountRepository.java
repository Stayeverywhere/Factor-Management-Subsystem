package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.auth.Account;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaAccountRepository implements AccountRepository {

    private final AccountJpaRepository jpa;

    public JpaAccountRepository(AccountJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return jpa.findByUsername(username).map(this::toDomain);
    }

    private Account toDomain(AccountEntity e) {
        return new Account(
                e.getId(), e.getUsername(), e.getPasswordHash(),
                e.getDisplayName(), UserType.valueOf(e.getUserType()),
                e.getRoleId(), e.getTenantId(), e.isEnabled()
        );
    }
}
