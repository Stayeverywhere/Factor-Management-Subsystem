package com.factor.infrastructure.persistence;

import com.factor.domain.trade.TradeCurrencyAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易银子账户仓储内存实现测试
 */
class InMemoryTradeCurrencyAccountRepositoryTest {

    private InMemoryTradeCurrencyAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTradeCurrencyAccountRepository();
    }

    @Test
    void testFindAll() {
        List<TradeCurrencyAccount> accounts = repository.findAll();
        assertNotNull(accounts);
        assertTrue(accounts.size() > 0);
    }

    @Test
    void testFindByTraderId() {
        List<TradeCurrencyAccount> accounts = repository.findByTraderId("a2");
        assertNotNull(accounts);
        assertTrue(accounts.size() > 0);
        accounts.forEach(account -> assertEquals("a2", account.traderId()));
    }

    @Test
    void testFindByTraderIdNotFound() {
        List<TradeCurrencyAccount> accounts = repository.findByTraderId("non-existent-trader");
        assertNotNull(accounts);
        assertEquals(0, accounts.size());
    }

    @Test
    void testFindByIdExisting() {
        Optional<TradeCurrencyAccount> account = repository.findById("ca1");
        assertTrue(account.isPresent());
        assertEquals("ca1", account.get().id());
        assertEquals("张三", account.get().customerName());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<TradeCurrencyAccount> account = repository.findById("non-existent-id");
        assertFalse(account.isPresent());
    }

    @Test
    void testSaveNewAccount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount newAccount = new TradeCurrencyAccount(
                null,
                "trader-new",
                "customer-new",
                "王五",
                "CNY",
                new BigDecimal("50000.00"),
                BigDecimal.ZERO,
                "OPEN",
                now,
                now
        );

        TradeCurrencyAccount saved = repository.save(newAccount);
        assertNotNull(saved.id());
        assertEquals("trader-new", saved.traderId());
        assertEquals("王五", saved.customerName());

        // 验证可以找到
        Optional<TradeCurrencyAccount> found = repository.findById(saved.id());
        assertTrue(found.isPresent());
    }

    @Test
    void testSaveExistingAccount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount existingAccount = new TradeCurrencyAccount(
                "ca1",
                "a2",
                "c1001",
                "张三更新",
                "CNY",
                new BigDecimal("150000.00"),
                BigDecimal.ZERO,
                "OPEN",
                now,
                now
        );

        TradeCurrencyAccount saved = repository.save(existingAccount);
        assertEquals("ca1", saved.id());
        assertEquals("张三更新", saved.customerName());
        assertEquals(new BigDecimal("150000.00"), saved.availableAmount());
    }

    @Test
    void testSaveMultipleAccounts() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount account1 = repository.save(new TradeCurrencyAccount(
                null, "trader-001", "customer-001", "用户1", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        TradeCurrencyAccount account2 = repository.save(new TradeCurrencyAccount(
                null, "trader-001", "customer-002", "用户2", "CNY",
                new BigDecimal("20000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        List<TradeCurrencyAccount> accounts = repository.findByTraderId("trader-001");
        assertTrue(accounts.size() >= 2);
    }

    @Test
    void testSaveAccountWithDifferentCurrencies() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount cnyAccount = repository.save(new TradeCurrencyAccount(
                null, "trader-multi", "customer-001", "多币种用户", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        TradeCurrencyAccount usdAccount = repository.save(new TradeCurrencyAccount(
                null, "trader-multi", "customer-001", "多币种用户", "USD",
                new BigDecimal("1000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        assertNotNull(cnyAccount.id());
        assertNotNull(usdAccount.id());
        assertNotEquals(cnyAccount.id(), usdAccount.id());
    }

    @Test
    void testFindByIdAfterUpdate() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount original = repository.findById("ca1").orElseThrow();
        BigDecimal originalAmount = original.availableAmount();

        TradeCurrencyAccount updated = repository.save(new TradeCurrencyAccount(
                "ca1",
                original.traderId(),
                original.customerId(),
                original.customerName(),
                original.currency(),
                originalAmount.add(new BigDecimal("10000.00")),
                original.frozenAmount(),
                original.status(),
                original.createdAt(),
                now
        ));

        Optional<TradeCurrencyAccount> found = repository.findById("ca1");
        assertTrue(found.isPresent());
        assertEquals(originalAmount.add(new BigDecimal("10000.00")), found.get().availableAmount());
    }

    @Test
    void testSaveAccountWithFrozenAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = repository.save(new TradeCurrencyAccount(
                null,
                "trader-frozen",
                "customer-frozen",
                "冻结测试用户",
                "CNY",
                new BigDecimal("80000.00"),
                new BigDecimal("20000.00"),
                "FROZEN",
                now,
                now
        ));

        Optional<TradeCurrencyAccount> found = repository.findById(account.id());
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("80000.00"), found.get().availableAmount());
        assertEquals(new BigDecimal("20000.00"), found.get().frozenAmount());
        assertEquals("FROZEN", found.get().status());
    }

    @Test
    void testFindAllReturnsImmutableList() {
        List<TradeCurrencyAccount> accounts = repository.findAll();
        assertNotNull(accounts);

        // 尝试修改列表应该抛出异常
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.add(new TradeCurrencyAccount(
                    "test-id", "test-trader", "test-customer", "测试", "CNY",
                    BigDecimal.ZERO, BigDecimal.ZERO, "OPEN", LocalDateTime.now(), LocalDateTime.now()
            ));
        });
    }

    @Test
    void testRepositoryPersistence() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount saved1 = repository.save(new TradeCurrencyAccount(
                null, "trader-persist", "customer-001", "持久化测试", "CNY",
                new BigDecimal("50000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        // 再次查询应该能找到
        Optional<TradeCurrencyAccount> found = repository.findById(saved1.id());
        assertTrue(found.isPresent());

        // 再次保存另一个账户
        TradeCurrencyAccount saved2 = repository.save(new TradeCurrencyAccount(
                null, "trader-persist", "customer-002", "持久化测试2", "CNY",
                new BigDecimal("30000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        // 两个都应该存在
        assertTrue(repository.findById(saved1.id()).isPresent());
        assertTrue(repository.findById(saved2.id()).isPresent());
    }

    @Test
    void testFindByTraderIdWithMultipleResults() {
        LocalDateTime now = LocalDateTime.now();

        repository.save(new TradeCurrencyAccount(
                null, "multi-trader", "customer-001", "客户1", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        repository.save(new TradeCurrencyAccount(
                null, "multi-trader", "customer-002", "客户2", "CNY",
                new BigDecimal("20000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        repository.save(new TradeCurrencyAccount(
                null, "multi-trader", "customer-003", "客户3", "USD",
                new BigDecimal("5000.00"), BigDecimal.ZERO, "OPEN", now, now
        ));

        List<TradeCurrencyAccount> accounts = repository.findByTraderId("multi-trader");
        assertTrue(accounts.size() >= 3);
    }

    @Test
    void testSaveAccountWithZeroAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = repository.save(new TradeCurrencyAccount(
                null,
                "trader-zero",
                "customer-zero",
                "零余额用户",
                "CNY",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "OPEN",
                now,
                now
        ));

        Optional<TradeCurrencyAccount> found = repository.findById(account.id());
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.ZERO, found.get().availableAmount());
        assertEquals(BigDecimal.ZERO, found.get().frozenAmount());
    }
}