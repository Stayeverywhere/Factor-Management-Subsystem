package com.factor.application.trade;

import com.factor.common.model.PageResult;
import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.TradeTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface TraderCurrencyService {
    PageResult<TradeCurrencyAccount> listAccounts(String traderId, long page, long size);

    TradeCurrencyAccount getAccount(String accountId);

    TradeCurrencyAccount openCurrencyAccount(String traderId, String customerId, String customerName, String currency);

    TradeTransaction freeze(String accountId, BigDecimal amount, String bizNo, String description);

    TradeTransaction unfreeze(String accountId, BigDecimal amount, String bizNo, String description);

    TradeTransaction transfer(String fromAccountId, String toAccountId, BigDecimal amount, String bizNo, String description);

    List<TradeTransaction> listTransactions(String accountId);
}
