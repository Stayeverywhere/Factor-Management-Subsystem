package com.factor.domain.trade;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易流水领域模型测试
 */
class TradeTransactionTest {

    @Test
    void testCreateTradeTransaction() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                "txn-001",
                "acc-001",
                "FREEZE",
                new BigDecimal("5000.00"),
                "BIZ-20240101-001",
                "冻结资金用于投资",
                now
        );

        assertEquals("txn-001", transaction.id());
        assertEquals("acc-001", transaction.currencyAccountId());
        assertEquals("FREEZE", transaction.transactionType());
        assertEquals(new BigDecimal("5000.00"), transaction.amount());
        assertEquals("BIZ-20240101-001", transaction.bizNo());
        assertEquals("冻结资金用于投资", transaction.description());
        assertNotNull(transaction.occurredAt());
    }

    @Test
    void testTransactionWithNullId() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                null,
                "acc-001",
                "FREEZE",
                new BigDecimal("5000.00"),
                "BIZ-20240101-001",
                "冻结资金",
                now
        );

        assertNull(transaction.id());
        assertEquals("acc-001", transaction.currencyAccountId());
        assertEquals("FREEZE", transaction.transactionType());
    }

    @Test
    void testTransactionTypes() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction freezeTransaction = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结", now
        );

        TradeTransaction unfreezeTransaction = new TradeTransaction(
                "txn-002", "acc-001", "UNFREEZE",
                new BigDecimal("5000.00"), "BIZ-002", "解冻", now
        );

        TradeTransaction transferTransaction = new TradeTransaction(
                "txn-003", "acc-001->acc-002", "TRANSFER",
                new BigDecimal("5000.00"), "BIZ-003", "转账", now
        );

        assertEquals("FREEZE", freezeTransaction.transactionType());
        assertEquals("UNFREEZE", unfreezeTransaction.transactionType());
        assertEquals("TRANSFER", transferTransaction.transactionType());
    }

    @Test
    void testTransactionEquality() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction1 = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结", now
        );

        TradeTransaction transaction2 = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结", now
        );

        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    void testTransactionToString() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("5000.00"), "BIZ-001", "冻结资金", now
        );

        String transactionString = transaction.toString();
        assertNotNull(transactionString);
        assertTrue(transactionString.contains("txn-001"));
        assertTrue(transactionString.contains("FREEZE"));
        assertTrue(transactionString.contains("5000.00"));
    }

    @Test
    void testTransactionWithZeroAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeTransaction transaction = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                BigDecimal.ZERO, "BIZ-001", "零金额交易", now
        );

        assertEquals(BigDecimal.ZERO, transaction.amount());
    }

    @Test
    void testTransactionWithLargeAmount() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal largeAmount = new BigDecimal("999999999999.99");
        TradeTransaction transaction = new TradeTransaction(
                "txn-001", "acc-001", "TRANSFER",
                largeAmount, "BIZ-001", "大额转账", now
        );

        assertEquals(largeAmount, transaction.amount());
    }

    @Test
    void testTransactionWithDifferentDescriptions() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction shortDesc = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "BIZ-001", "冻结", now
        );

        TradeTransaction longDesc = new TradeTransaction(
                "txn-002", "acc-001", "TRANSFER",
                new BigDecimal("1000.00"), "BIZ-002",
                "转账用于购买基金产品，投资组合编号：PORTFOLIO-2024-001", now
        );

        assertEquals("冻结", shortDesc.description());
        assertTrue(longDesc.description().length() > 20);
    }

    @Test
    void testTransferTransactionCurrencyAccountId() {
        LocalDateTime now = LocalDateTime.now();
        String currencyAccountId = "acc-001->acc-002";

        TradeTransaction transaction = new TradeTransaction(
                "txn-001",
                currencyAccountId,
                "TRANSFER",
                new BigDecimal("10000.00"),
                "BIZ-001",
                "账户间转账",
                now
        );

        assertEquals("acc-001->acc-002", transaction.currencyAccountId());
        assertTrue(transaction.currencyAccountId().contains("->"));
    }

    @Test
    void testTransactionBizNoFormat() {
        LocalDateTime now = LocalDateTime.now();

        TradeTransaction transaction1 = new TradeTransaction(
                "txn-001", "acc-001", "FREEZE",
                new BigDecimal("1000.00"), "FREEZE-20240101-001", "冻结", now
        );

        TradeTransaction transaction2 = new TradeTransaction(
                "txn-002", "acc-001", "TRANSFER",
                new BigDecimal("1000.00"), "TRANSFER-20240101-002", "转账", now
        );

        assertTrue(transaction1.bizNo().startsWith("FREEZE"));
        assertTrue(transaction2.bizNo().startsWith("TRANSFER"));
    }
}