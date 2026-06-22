package com.factor.domain.trade.repository;

import com.factor.domain.trade.TradeTransaction;

import java.util.List;

public interface TradeTransactionRepository {
    List<TradeTransaction> findByCurrencyAccountId(String currencyAccountId);

    TradeTransaction save(TradeTransaction transaction);
}
