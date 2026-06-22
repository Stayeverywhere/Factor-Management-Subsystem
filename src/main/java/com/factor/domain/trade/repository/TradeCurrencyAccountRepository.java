package com.factor.domain.trade.repository;

import com.factor.domain.trade.TradeCurrencyAccount;

import java.util.List;
import java.util.Optional;

public interface TradeCurrencyAccountRepository {
    List<TradeCurrencyAccount> findAll();

    List<TradeCurrencyAccount> findByTraderId(String traderId);

    Optional<TradeCurrencyAccount> findById(String id);

    TradeCurrencyAccount save(TradeCurrencyAccount account);
}
