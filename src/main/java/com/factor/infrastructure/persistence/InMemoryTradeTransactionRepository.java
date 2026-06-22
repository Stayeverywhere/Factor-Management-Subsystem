package com.factor.infrastructure.persistence;

import com.factor.domain.trade.TradeTransaction;
import com.factor.domain.trade.repository.TradeTransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryTradeTransactionRepository implements TradeTransactionRepository {

    private final List<TradeTransaction> storage = new ArrayList<>();

    @Override
    public List<TradeTransaction> findByCurrencyAccountId(String currencyAccountId) {
        return storage.stream().filter(item -> item.currencyAccountId().equals(currencyAccountId)).toList();
    }

    @Override
    public TradeTransaction save(TradeTransaction transaction) {
        TradeTransaction stored = transaction.id() == null ? new TradeTransaction(UUID.randomUUID().toString(), transaction.currencyAccountId(), transaction.transactionType(), transaction.amount(), transaction.bizNo(), transaction.description(), transaction.occurredAt()) : transaction;
        storage.add(stored);
        return stored;
    }
}
