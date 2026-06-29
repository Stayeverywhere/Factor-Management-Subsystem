package com.factor.infrastructure.persistence;

import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.repository.TradeCurrencyAccountRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryTradeCurrencyAccountRepository implements TradeCurrencyAccountRepository {

    private final List<TradeCurrencyAccount> storage = new ArrayList<>(List.of(
            new TradeCurrencyAccount("ca1", "a2", "c1001", "张三", "CNY", new BigDecimal("100000.00"), new BigDecimal("0.00"), "OPEN", LocalDateTime.now(), LocalDateTime.now())
    ));

    @Override
    public List<TradeCurrencyAccount> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public List<TradeCurrencyAccount> findByTraderId(String traderId) {
        return storage.stream().filter(item -> item.traderId().equals(traderId)).toList();
    }

    @Override
    public Optional<TradeCurrencyAccount> findById(String id) {
        return storage.stream().filter(item -> item.id().equals(id)).findFirst();
    }

    @Override
    public TradeCurrencyAccount save(TradeCurrencyAccount account) {
        TradeCurrencyAccount stored = account.id() == null ? new TradeCurrencyAccount(UUID.randomUUID().toString(), account.traderId(), account.customerId(), account.customerName(), account.currency(), account.availableAmount(), account.frozenAmount(), account.status(), account.createdAt(), LocalDateTime.now()) : account;
        storage.removeIf(item -> item.id().equals(stored.id()));
        storage.add(stored);
        return stored;
    }
}
