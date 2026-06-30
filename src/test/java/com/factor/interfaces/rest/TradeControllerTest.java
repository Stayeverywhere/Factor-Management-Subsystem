package com.factor.interfaces.rest;

import com.factor.application.trade.TraderCurrencyService;
import com.factor.application.trade.TraderCurrencyServiceImpl;
import com.factor.common.api.ApiResponse;
import com.factor.common.model.PageResult;
import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.TradeTransaction;
import com.factor.infrastructure.persistence.InMemoryTradeCurrencyAccountRepository;
import com.factor.infrastructure.persistence.InMemoryTradeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易控制器测试
 */
class TradeControllerTest {

    private TradeController controller;
    private TraderCurrencyService service;
    private InMemoryTradeCurrencyAccountRepository accountRepository;
    private InMemoryTradeTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryTradeCurrencyAccountRepository();
        transactionRepository = new InMemoryTradeTransactionRepository();
        service = new TraderCurrencyServiceImpl(accountRepository, transactionRepository);
        controller = new TradeController(service);
    }

    @Test
    void testListAccountsSuccess() {
        ApiResponse<PageResult<TradeCurrencyAccount>> response = controller.listAccounts("a2", 1, 10);

        assertTrue(response.success());
        assertNotNull(response.data());
        assertTrue(response.data().items().size() > 0);
    }

    @Test
    void testListAccountsEmpty() {
        ApiResponse<PageResult<TradeCurrencyAccount>> response = controller.listAccounts("trader-empty", 1, 10);

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(0, response.data().items().size());
    }

    @Test
    void testListAccountsMultipleAccounts() {
        // 创建多个账户
        service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        service.openCurrencyAccount("trader-001", "customer-002", "李四", "CNY");
        service.openCurrencyAccount("trader-001", "customer-003", "王五", "USD");

        ApiResponse<PageResult<TradeCurrencyAccount>> response = controller.listAccounts("trader-001", 1, 10);

        assertTrue(response.success());
        assertEquals(3, response.data().items().size());
    }

    @Test
    void testListTransactionsSuccess() {
        // 创建账户并设置余额
        TradeCurrencyAccount account = service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        // 创建交易
        service.freeze(account.id(), new BigDecimal("5000.00"), "BIZ-001", "冻结");
        service.freeze(account.id(), new BigDecimal("3000.00"), "BIZ-002", "冻结2");

        ApiResponse<List<TradeTransaction>> response = controller.listTransactions(account.id());

        assertTrue(response.success());
        assertNotNull(response.data());
        assertTrue(response.data().size() >= 2);
    }

    @Test
    void testListTransactionsEmpty() {
        ApiResponse<List<TradeTransaction>> response = controller.listTransactions("acc-999");

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(0, response.data().size());
    }

    @Test
    void testOpenAccountSuccess() {
        ApiResponse<TradeCurrencyAccount> response = controller.open(
                new com.factor.interfaces.rest.dto.CurrencyOpenRequest(
                        "trader-001", "customer-001", "新用户", "CNY"
                )
        );

        assertTrue(response.success());
        assertNotNull(response.data());
        assertNotNull(response.data().id());
        assertEquals("trader-001", response.data().traderId());
        assertEquals("customer-001", response.data().customerId());
        assertEquals("新用户", response.data().customerName());
        assertEquals("CNY", response.data().currency());
    }

    @Test
    void testOpenAccountWithDifferentCurrency() {
        ApiResponse<TradeCurrencyAccount> response = controller.open(
                new com.factor.interfaces.rest.dto.CurrencyOpenRequest(
                        "trader-001", "customer-001", "用户", "USD"
                )
        );

        assertTrue(response.success());
        assertEquals("USD", response.data().currency());
    }

    @Test
    void testFreezeSuccess() {
        // 创建账户并设置余额
        TradeCurrencyAccount account = service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        ApiResponse<TradeTransaction> response = controller.freeze(
                new com.factor.interfaces.rest.dto.FreezeRequest(
                        account.id(), new BigDecimal("5000.00"), "BIZ-001", "冻结资金"
                )
        );

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals("FREEZE", response.data().transactionType());
        assertEquals(new BigDecimal("5000.00"), response.data().amount());
    }

    @Test
    void testUnfreezeSuccess() {
        // 创建账户并设置冻结金额
        TradeCurrencyAccount account = service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        accountRepository.save(new TradeCurrencyAccount(
                account.id(), account.traderId(), account.customerId(), account.customerName(),
                account.currency(), new BigDecimal("10000.00"), new BigDecimal("5000.00"), "OPEN",
                account.createdAt(), LocalDateTime.now()
        ));

        ApiResponse<TradeTransaction> response = controller.unfreeze(
                new com.factor.interfaces.rest.dto.FreezeRequest(
                        account.id(), new BigDecimal("5000.00"), "BIZ-001", "解冻资金"
                )
        );

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals("UNFREEZE", response.data().transactionType());
    }

    @Test
    void testTransferSuccess() {
        // 创建两个账户并设置余额
        TradeCurrencyAccount fromAccount = service.openCurrencyAccount("trader-001", "customer-001", "张三", "CNY");
        TradeCurrencyAccount toAccount = service.openCurrencyAccount("trader-001", "customer-002", "李四", "CNY");

        accountRepository.save(new TradeCurrencyAccount(
                fromAccount.id(), fromAccount.traderId(), fromAccount.customerId(), fromAccount.customerName(),
                fromAccount.currency(), new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN",
                fromAccount.createdAt(), LocalDateTime.now()
        ));

        ApiResponse<TradeTransaction> response = controller.transfer(
                new com.factor.interfaces.rest.dto.CurrencyTransferRequest(
                        fromAccount.id(), toAccount.id(), new BigDecimal("10000.00"), "BIZ-001", "账户间转账"
                )
        );

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals("TRANSFER", response.data().transactionType());
        assertEquals(new BigDecimal("10000.00"), response.data().amount());
    }

    @Test
    void testGetAccountSuccess() {
        ApiResponse<PageResult<TradeCurrencyAccount>> response = controller.listAccounts("a2", 1, 10);

        assertTrue(response.success());
        assertNotNull(response.data());
        assertTrue(response.data().items().size() > 0);

        TradeCurrencyAccount account = response.data().items().get(0);
        assertEquals("ca1", account.id());
        assertEquals("张三", account.customerName());
    }
}