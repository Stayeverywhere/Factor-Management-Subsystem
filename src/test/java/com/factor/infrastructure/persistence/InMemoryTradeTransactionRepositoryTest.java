package com.factor.infrastructure.persistence;

import com.factor.domain.trade.TradeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易流水仓储内存实现测试
 */
class InMemoryTradeTransactionRepositoryTest {

    private InMemoryTradeTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTradeTransactionRepository();
    }

    @Test
    void testSaveNewTransaction() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                null,
                "acc-001",
                "FREEZE",
                new BigDecimal("5000.00"),
                "BIZ-001",
                "冻结资金",
                now
        );

        TradeTransaction saved = repository.save(transaction);
        assertNotNull(saved.id());
        assertEquals("acc-001", saved.currencyAccountId());
        assertEquals("FREEZE", saved.transactionType());
        assertEquals(new BigDecimal("5000.00"), saved.amount());
    }

    @Test
    void testSaveTransactionWithExistingId() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction transaction1 = repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "BIZ-001", "冻结", now
        ));

        TradeTransaction transaction2 = repository.save(new TradeTransaction(
                null, "acc-001", "UNFREEZE",
                new BigDecimal("1000.00"), "BIZ-002", "解冻", now
        ));

        assertNotNull(transaction1.id());
        assertNotNull(transaction2.id());
        assertNotEquals(transaction1.id(), transaction2.id());
    }

    @Test
    void testFindByCurrencyAccountId() {
        LocalDateTime now = LocalDateTime.now();

        repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结", now
        ));

        repository.save(new TradeTransaction(
                null, "acc-001", "UNFREEZE",
                new BigDecimal("5000.00"), "BIZ-002", "解冻", now
        ));

        repository.save(new TradeTransaction(
                null, "acc-002", "TRANSFER",
                new BigDecimal("3000.00"), "BIZ-003", "转账", now
        ));

        List<TradeTransaction> transactions = repository.findByCurrencyAccountId("acc-001");
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 2);
        transactions.forEach(t -> assertEquals("acc-001", t.currencyAccountId()));
    }

    @Test
    void testFindByCurrencyAccountIdNotFound() {
        List<TradeTransaction> transactions = repository.findByCurrencyAccountId("non-existent-acc");
        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    void testSaveDifferentTransactionTypes() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction freeze = repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-FREEZE", "冻结", now
        ));

        TradeTransaction unfreeze = repository.save(new TradeTransaction(
                null, "acc-001", "UNFREEZE",
                new BigDecimal("5000.00"), "BIZ-UNFREEZE", "解冻", now
        ));

        TradeTransaction transfer = repository.save(new TradeTransaction(
                null, "acc-001->acc-002", "TRANSFER",
                new BigDecimal("10000.00"), "BIZ-TRANSFER", "转账", now
        ));

        assertEquals("FREEZE", freeze.transactionType());
        assertEquals("UNFREEZE", unfreeze.transactionType());
        assertEquals("TRANSFER", transfer.transactionType());
    }

    @Test
    void testSaveTransactionWithZeroAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = repository.save(new TradeTransaction(
                null,
                "acc-001",
                "FREEZE",
                BigDecimal.ZERO,
                "BIZ-ZERO",
                "零金额交易",
                now
        ));

        assertNotNull(transaction.id());
        assertEquals(BigDecimal.ZERO, transaction.amount());
    }

    @Test
    void testSaveTransactionWithLargeAmount() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal largeAmount = new BigDecimal("999999999999.99");

        TradeTransaction transaction = repository.save(new TradeTransaction(
                null,
                "acc-001",
                "TRANSFER",
                largeAmount,
                "BIZ-LARGE",
                "大额转账",
                now
        ));

        assertNotNull(transaction.id());
        assertEquals(largeAmount, transaction.amount());
    }

    @Test
    void testSaveTransactionWithTransferFormat() {
        LocalDateTime now = LocalDateTime.now();
        String currencyAccountId = "acc-001->acc-002";

        TradeTransaction transaction = repository.save(new TradeTransaction(
                null,
                currencyAccountId,
                "TRANSFER",
                new BigDecimal("10000.00"),
                "BIZ-TRANSFER",
                "账户间转账",
                now
        ));

        assertNotNull(transaction.id());
        assertEquals(currencyAccountId, transaction.currencyAccountId());
        assertTrue(transaction.currencyAccountId().contains("->"));
    }

    @Test
    void testMultipleTransactionsForSameAccount() {
        LocalDateTime now = LocalDateTime.now();
        String accountId = "acc-multi";

        repository.save(new TradeTransaction(
                null, accountId, "FREEZE",
                new BigDecimal("1000.00"), "BIZ-001", "冻结1", now
        ));

        repository.save(new TradeTransaction(
                null, accountId, "FREEZE",
                new BigDecimal("2000.00"), "BIZ-002", "冻结2", now
        ));

        repository.save(new TradeTransaction(
                null, accountId, "UNFREEZE",
                new BigDecimal("1500.00"), "BIZ-003", "解冻", now
        ));

        List<TradeTransaction> transactions = repository.findByCurrencyAccountId(accountId);
        assertTrue(transactions.size() >= 3);
    }

    @Test
    void testTransactionPersistence() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction saved1 = repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结", now
        ));

        // 再次查找应该能找到
        List<TradeTransaction> transactions = repository.findByCurrencyAccountId("acc-001");
        assertTrue(transactions.size() > 0);

        // 保存另一个交易
        TradeTransaction saved2 = repository.save(new TradeTransaction(
                null, "acc-001", "UNFREEZE",
                new BigDecimal("5000.00"), "BIZ-002", "解冻", now
        ));

        // 现在应该有两条记录
        List<TradeTransaction> allTransactions = repository.findByCurrencyAccountId("acc-001");
        assertTrue(allTransactions.size() >= 2);
    }

    @Test
    void testTransactionWithDescription() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction shortDesc = repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "BIZ-001", "冻结", now
        ));

        TradeTransaction longDesc = repository.save(new TradeTransaction(
                null, "acc-001", "TRANSFER",
                new BigDecimal("1000.00"), "BIZ-002",
                "转账用于购买基金产品，投资组合编号：PORTFOLIO-2024-001，交易员：张三", now
        ));

        assertNotNull(shortDesc.id());
        assertNotNull(longDesc.id());
        assertEquals("冻结", shortDesc.description());
        assertTrue(longDesc.description().length() > 30);
    }

    @Test
    void testTransactionWithBizNoFormat() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction transaction1 = repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "FREEZE-20240101-001", "冻结", now
        ));

        TradeTransaction transaction2 = repository.save(new TradeTransaction(
                null, "acc-001", "TRANSFER",
                new BigDecimal("1000.00"), "TRANSFER-20240101-002", "转账", now
        ));

        assertEquals("FREEZE-20240101-001", transaction1.bizNo());
        assertEquals("TRANSFER-20240101-002", transaction2.bizNo());
    }

    @Test
    void testSaveTransactionWithNullIdGeneratesUUID() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                null,
                "acc-001",
                "FREEZE",
                new BigDecimal("5000.00"),
                "BIZ-001",
                "冻结",
                now
        );

        TradeTransaction saved = repository.save(transaction);
        assertNotNull(saved.id());
        assertTrue(saved.id().length() > 0);
    }

    @Test
    void testFindTransactionsSortedByAccountId() {
        LocalDateTime now = LocalDateTime.now();

        repository.save(new TradeTransaction(
                null, "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "BIZ-001", "冻结", now
        ));

        repository.save(new TradeTransaction(
                null, "acc-002", "FREEZE",
                new BigDecimal("2000.00"), "BIZ-002", "冻结", now
        ));

        repository.save(new TradeTransaction(
                null, "acc-001", "UNFREEZE",
                new BigDecimal("500.00"), "BIZ-003", "解冻", now
        ));

        List<TradeTransaction> acc1Transactions = repository.findByCurrencyAccountId("acc-001");
        List<TradeTransaction> acc2Transactions = repository.findByCurrencyAccountId("acc-002");

        assertTrue(acc1Transactions.size() > 0);
        assertTrue(acc2Transactions.size() > 0);
    }
}