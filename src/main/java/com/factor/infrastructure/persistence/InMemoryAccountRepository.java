package com.factor.infrastructure.persistence;

import com.factor.domain.auth.Account;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
@Profile("!jpa")
@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final List<Account> accounts = List.of(
            new Account("a1", "admin", "admin123", "系统超级管理员", UserType.SYSTEM_ADMIN, "r1", null, true),
            new Account("a2", "trader", "trader123", "交易员", UserType.TRADER, "r2", "t1", true),
            new Account("a3", "customer", "customer123", "客户", UserType.CUSTOMER, "r3", "t1", true)
    );

    @Override
    public Optional<Account> findByUsername(String username) {
        return accounts.stream().filter(account -> account.username().equals(username)).findFirst();
    }
}
