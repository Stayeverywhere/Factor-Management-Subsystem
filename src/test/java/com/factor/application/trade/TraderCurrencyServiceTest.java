package com.factor.application.trade;

import com.factor.common.exception.BusinessException;
import com.factor.common.model.PageResult;
import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.TradeTransaction;
import com.factor.domain.trade.repository.TradeCurrencyAccountRepository;
import com.factor.domain.trade.repository.TradeTransactionRepository;
import com.factor.infrastructure.persistence.InMemoryTradeCurrencyAccountRepository;
import com.factor.infrastructure.persistence.InMemoryTradeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易银子应用服务测试
 */
class TraderCurrencyServiceTest {

    private TraderCurrencyService service;
    private InMemoryTradeCurrencyAccountRepository accountRepository;
    private InMemoryTradeTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryTradeCurrencyAccountRepository();
        transactionRepository = new InMemoryTradeTransactionRepository();
        service = new TraderCurrencyServiceImpl(accountRepository, transactionRepository);
    }

    // ========== listAccounts 测试 ==========

    @Test
    void testListAccountsSuccess() {
        PageResult<TradeCurrencyAccount> result = service.listAccounts("a2", 1, 10);

        assertNotNull(result);
        assertTrue(result.items().size() > 0);
        assertEquals(1, result.page());
        assertEquals(10, result.size());
    }

    @Test
    void testListAccountsEmpty() {
        PageResult<TradeCurrencyAccount> result = service.listAccounts("trader-empty", 1, 10);

        assertNotNull(result);
        assertEquals(0, result.items().size());
        assertEquals(0, result.total());
    }

    @Test
    void testListAccountsMultipleAccounts() {
        // 先创建多个账户
        LocalDateTime now = LocalDateTime.now();
        service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        service.openCurrencyAccount("trader-001", "customer-002", "李四", "CNY");
        service.openCurrencyAccount("trader-001", "customer-003", "王五", "USD");

        PageResult<TradeCurrencyAccount> result = service.listAccounts("trader-001", 1, 10);

        assertEquals(3, result.items().size());
        assertEquals(3, result.total());
    }

    // ========== getAccount 测试 ==========

    @Test
    void testGetAccountSuccess() {
        TradeCurrencyAccount result = service.getAccount("ca1");

        assertNotNull(result);
        assertEquals("ca1", result.id());
        assertEquals("张三", result.customerName());
    }

    @Test
    void testGetAccountNotFound() {
        assertThrows(BusinessException.class, () -> {
            service.getAccount("acc-999");
        });
    }

    // ========== openCurrencyAccount 测试 ==========

    @Test
    void testOpenCurrencyAccountSuccess() {
        TradeCurrencyAccount result = service.openCurrencyAccount(
                "trader-001", "customer-001", "新用户", "CNY"
        );

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("trader-001", result.traderId());
        assertEquals("customer-001", result.customerId());
        assertEquals("新用户", result.customerName());
        assertEquals("CNY", result.currency());
        assertEquals(BigDecimal.ZERO, result.availableAmount());
        assertEquals(BigDecimal.ZERO, result.frozenAmount());
        assertEquals("OPEN", result.status());
    }

    @Test
    void testOpenCurrencyAccountWithDifferentCurrencies() {
        TradeCurrencyAccount cnyAccount = service.openCurrencyAccount(
                "trader-001", "customer-001", "用户", "CNY"
        );
        TradeCurrencyAccount usdAccount = service.openCurrencyAccount(
                "trader-001", "customer-001", "用户", "USD"
        );

        assertEquals("CNY", cnyAccount.currency());
        assertEquals("USD", usdAccount.currency());
    }

    // ========== freeze 测试 ==========

    @Test
    void testFreezeSuccess() {
        // 先开户并添加余额
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        // 手动设置余额（通过repository）
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        BigDecimal freezeAmount = new BigDecimal("5000.00");
        TradeTransaction result = service.freeze(account.id(), freezeAmount, "BIZ-001", "冻结资金");

        assertNotNull(result);
        assertEquals("FREEZE", result.transactionType());
        assertEquals(freezeAmount, result.amount());
    }

    @Test
    void testFreezeInsufficientAvailableAmount() {
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        // 设置较小的余额
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("1000.00"), BigDecimal.ZERO, "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        BigDecimal freezeAmount = new BigDecimal("2000.00");

        assertThrows(BusinessException.class, () -> {
            service.freeze(account.id(), freezeAmount, "BIZ-001", "冻结资金");
        });
    }

    @Test
    void testFreezeZeroAmount() {
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        assertThrows(BusinessException.class, () -> {
            service.freeze(account.id(), new BigDecimal("1000.00"), "BIZ-001", "冻结资金");
        });
    }

    // ========== unfreeze 测试 ==========

    @Test
    void testUnfreezeSuccess() {
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        // 设置冻结金额
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), new BigDecimal("5000.00"), "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        BigDecimal unfreezeAmount = new BigDecimal("5000.00");
        TradeTransaction result = service.unfreeze(account.id(), unfreezeAmount, "BIZ-001", "解冻资金");

        assertNotNull(result);
        assertEquals("UNFREEZE", result.transactionType());
        assertEquals(unfreezeAmount, result.amount());
    }

    @Test
    void testUnfreezeInsufficientFrozenAmount() {
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        // 设置较小的冻结金额
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), new BigDecimal("1000.00"), "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        BigDecimal unfreezeAmount = new BigDecimal("2000.00");

        assertThrows(BusinessException.class, () -> {
            service.unfreeze(account.id(), unfreezeAmount, "BIZ-001", "解冻资金");
        });
    }

    // ========== transfer 测试 ==========

    @Test
    void testTransferSuccess() {
        TradeCurrencyAccount fromAccount = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );
        TradeCurrencyAccount toAccount = service.openCurrencyAccount(
                "trader-001", "customer-002", "李四", "CNY"
        );

        // 设置转出账户余额
        accountRepository.save(new TradeCurrencyAccount(
                fromAccount.id(), fromAccount.traderId(), fromAccount.customerId(), fromAccount.customerName(),
                fromAccount.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                fromAccount.createdAt(), LocalDateTime.now()
        ));

        BigDecimal transferAmount = new BigDecimal("5000.00");
        TradeTransaction result = service.transfer(fromAccount.id(), toAccount.id(),
                transferAmount, "BIZ-001", "账户间转账");

        assertNotNull(result);
        assertEquals("TRANSFER", result.transactionType());
        assertEquals(transferAmount, result.amount());
        assertTrue(result.currencyAccountId().contains(fromAccount.id()));
        assertTrue(result.currencyAccountId().contains(toAccount.id()));
    }

    @Test
    void testTransferInsufficientAmount() {
        TradeCurrencyAccount fromAccount = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );
        TradeCurrencyAccount toAccount = service.openCurrencyAccount(
                "trader-001", "customer-002", "李四", "CNY"
        );

        // 设置较小的余额
        accountRepository.save(new TradeCurrencyAccount(
                fromAccount.id(), fromAccount.traderId(), fromAccount.customerId(), fromAccount.customerName(),
                fromAccount.currency(), new BigDecimal("1000.00"), BigDecimal.ZERO, "OPEN",
                fromAccount.createdAt(), LocalDateTime.now()
        ));

        BigDecimal transferAmount = new BigDecimal("2000.00");

        assertThrows(BusinessException.class, () -> {
            service.transfer(fromAccount.id(), toAccount.id(), transferAmount, "BIZ-001", "转账");
        });
    }

    // ========== listTransactions 测试 ==========

    @Test
    void testListTransactionsSuccess() {
        TradeCurrencyAccount account = service.openCurrencyAccount(
                "trader-001", "customer-001", "张三", "CNY"
        );

        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        // 创建一些交易
        service.freeze(account.id(), new BigDecimal("5000.00"), "BIZ-001", "冻结");
        service.freeze(account.id(), new BigDecimal("3000.00"), "BIZ-002", "冻结2");

        List<TradeTransaction> result = service.listTransactions(account.id());

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void testListTransactionsEmpty() {
        List<TradeTransaction> result = service.listTransactions("acc-999");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ========== 异常场景测试 ==========

    @Test
    void testFreezeWithNonExistentAccount() {
        assertThrows(BusinessException.class, () -> {
            service.freeze("acc-999", new BigDecimal("1000.00"), "BIZ-001", "冻结");
        });
    }

    @Test
    void testUnfreezeWithNonExistentAccount() {
        assertThrows(BusinessException.class, () -> {
            service.unfreeze("acc-999", new BigDecimal("1000.00"), "BIZ-001", "解冻");
        });
    }

    @Test
    void testTransferFromNonExistentAccount() {
        assertThrows(BusinessException.class, () -> {
            service.transfer("acc-999", "acc-002", new BigDecimal("1000.00"), "BIZ-001", "转账");
        });
    }
}