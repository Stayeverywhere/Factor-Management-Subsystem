package com.factor.domain.trade;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易银子账户领域模型测试
 */
class TradeCurrencyAccountTest {

    @Test
    void testCreateTradeCurrencyAccount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                "acc-001",
                "trader-001",
                "customer-001",
                "张三",
                "CNY",
                new BigDecimal("10000.00"),
                new BigDecimal("0.00"),
                "OPEN",
                now,
                now
        );

        assertEquals("acc-001", account.id());
        assertEquals("trader-001", account.traderId());
        assertEquals("customer-001", account.customerId());
        assertEquals("张三", account.customerName());
        assertEquals("CNY", account.currency());
        assertEquals(new BigDecimal("10000.00"), account.availableAmount());
        assertEquals(new BigDecimal("0.00"), account.frozenAmount());
        assertEquals("OPEN", account.status());
        assertNotNull(account.createdAt());
        assertNotNull(account.updatedAt());
    }

    @Test
    void testAccountWithNullId() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                null,
                "trader-001",
                "customer-001",
                "李四",
                "CNY",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "OPEN",
                now,
                now
        );

        assertNull(account.id());
        assertEquals("trader-001", account.traderId());
        assertEquals("customer-001", account.customerId());
    }

    @Test
    void testAccountWithDifferentCurrencies() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount cnyAccount = new TradeCurrencyAccount(
                "acc-cny", "trader-001", "customer-001", "张三", "CNY",
                new BigDecimal("100000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        TradeCurrencyAccount usdAccount = new TradeCurrencyAccount(
                "acc-usd", "trader-001", "customer-001", "张三", "USD",
                new BigDecimal("15000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        assertEquals("CNY", cnyAccount.currency());
        assertEquals("USD", usdAccount.currency());
    }

    @Test
    void testAccountStatus() {
        LocalDateTime now = LocalDateTime.now();

        TradeCurrencyAccount openAccount = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        TradeCurrencyAccount frozenAccount = new TradeCurrencyAccount(
                "acc-002", "trader-001", "customer-002", "李四", "CNY",
                new BigDecimal("0.00"), new BigDecimal("5000.00"), "FROZEN", now, now
        );

        assertEquals("OPEN", openAccount.status());
        assertEquals("FROZEN", frozenAccount.status());
    }

    @Test
    void testAccountWithFrozenAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                "acc-001",
                "trader-001",
                "customer-001",
                "张三",
                "CNY",
                new BigDecimal("80000.00"),
                new BigDecimal("20000.00"),
                "OPEN",
                now,
                now
        );

        assertEquals(new BigDecimal("80000.00"), account.availableAmount());
        assertEquals(new BigDecimal("20000.00"), account.frozenAmount());
    }

    @Test
    void testAccountEquality() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account1 = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        TradeCurrencyAccount account2 = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    void testAccountToString() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                new BigDecimal("10000.00"), BigDecimal.ZERO, "OPEN", now, now
        );

        String accountString = account.toString();
        assertNotNull(accountString);
        assertTrue(accountString.contains("acc-001"));
        assertTrue(accountString.contains("张三"));
        assertTrue(accountString.contains("CNY"));
    }

    @Test
    void testAccountWithZeroAmount() {
        LocalDateTime now = LocalDateTime.now();
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                BigDecimal.ZERO, BigDecimal.ZERO, "OPEN", now, now
        );

        assertEquals(BigDecimal.ZERO, account.availableAmount());
        assertEquals(BigDecimal.ZERO, account.frozenAmount());
    }

    @Test
    void testAccountWithLargeAmount() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal largeAmount = new BigDecimal("999999999999.99");
        TradeCurrencyAccount account = new TradeCurrencyAccount(
                "acc-001", "trader-001", "customer-001", "张三", "CNY",
                largeAmount, BigDecimal.ZERO, "OPEN", now, now
        );

        assertEquals(largeAmount, account.availableAmount());
    }
}