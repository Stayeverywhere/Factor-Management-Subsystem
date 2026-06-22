package com.factor.application.trade;

import com.factor.common.exception.BusinessException;
import com.factor.common.model.PageResult;
import com.factor.domain.auth.UserType;
import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.TradeTransaction;
import com.factor.domain.trade.repository.TradeCurrencyAccountRepository;
import com.factor.domain.trade.repository.TradeTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TraderCurrencyServiceImpl implements TraderCurrencyService {

    private final TradeCurrencyAccountRepository accountRepository;
    private final TradeTransactionRepository transactionRepository;

    public TraderCurrencyServiceImpl(TradeCurrencyAccountRepository accountRepository, TradeTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public PageResult<TradeCurrencyAccount> listAccounts(String traderId, long page, long size) {
        List<TradeCurrencyAccount> items = accountRepository.findByTraderId(traderId);
        return new PageResult<>(items, page, size, items.size());
    }

    @Override
    public TradeCurrencyAccount getAccount(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new BusinessException("银子账户不存在"));
    }

    @Override
    public TradeCurrencyAccount openCurrencyAccount(String traderId, String customerId, String customerName, String currency) {
        return accountRepository.save(new TradeCurrencyAccount(null, traderId, customerId, customerName, currency, BigDecimal.ZERO, BigDecimal.ZERO, "OPEN", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Override
    public TradeTransaction freeze(String accountId, BigDecimal amount, String bizNo, String description) {
        TradeCurrencyAccount account = getAccount(accountId);
        if (account.availableAmount().compareTo(amount) < 0) {
            throw new BusinessException("可用金额不足");
        }
        accountRepository.save(new TradeCurrencyAccount(account.id(), account.traderId(), account.customerId(), account.customerName(), account.currency(), account.availableAmount().subtract(amount), account.frozenAmount().add(amount), account.status(), account.createdAt(), LocalDateTime.now()));
        return transactionRepository.save(new TradeTransaction(null, accountId, "FREEZE", amount, bizNo, description, LocalDateTime.now()));
    }

    @Override
    public TradeTransaction unfreeze(String accountId, BigDecimal amount, String bizNo, String description) {
        TradeCurrencyAccount account = getAccount(accountId);
        if (account.frozenAmount().compareTo(amount) < 0) {
            throw new BusinessException("冻结金额不足");
        }
        accountRepository.save(new TradeCurrencyAccount(account.id(), account.traderId(), account.customerId(), account.customerName(), account.currency(), account.availableAmount().add(amount), account.frozenAmount().subtract(amount), account.status(), account.createdAt(), LocalDateTime.now()));
        return transactionRepository.save(new TradeTransaction(null, accountId, "UNFREEZE", amount, bizNo, description, LocalDateTime.now()));
    }

    @Override
    public TradeTransaction transfer(String fromAccountId, String toAccountId, BigDecimal amount, String bizNo, String description) {
        freeze(fromAccountId, amount, bizNo, description);
        unfreeze(fromAccountId, amount, bizNo, description);
        return transactionRepository.save(new TradeTransaction(null, fromAccountId + "->" + toAccountId, "TRANSFER", amount, bizNo, description, LocalDateTime.now()));
    }

    @Override
    public List<TradeTransaction> listTransactions(String accountId) {
        return transactionRepository.findByCurrencyAccountId(accountId);
    }
}
