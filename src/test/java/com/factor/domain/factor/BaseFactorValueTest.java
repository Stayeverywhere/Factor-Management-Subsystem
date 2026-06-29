package com.factor.domain.factor;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础因子值领域模型测试
 */
class BaseFactorValueTest {

    @Test
    void testCreateBaseFactorValue() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001",
                "000001",
                "bf-001",
                dataDate,
                new BigDecimal("1.2345"),
                updatedAt
        );

        assertEquals("bfv-001", value.id());
        assertEquals("000001", value.fundCode());
        assertEquals("bf-001", value.baseFactorId());
        assertEquals(dataDate, value.dataDate());
        assertEquals(new BigDecimal("1.2345"), value.value());
        assertEquals(updatedAt, value.updatedAt());
    }

    @Test
    void testBaseFactorValueWithNullId() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                null,
                "000001",
                "bf-001",
                dataDate,
                new BigDecimal("1.2345"),
                updatedAt
        );

        assertNull(value.id());
        assertEquals("000001", value.fundCode());
        assertEquals("bf-001", value.baseFactorId());
    }

    @Test
    void testBaseFactorValueWithDifferentFunds() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value1 = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("1.2345"), updatedAt
        );

        BaseFactorValue value2 = new BaseFactorValue(
                "bfv-002", "000002", "bf-001", dataDate, new BigDecimal("2.5678"), updatedAt
        );

        assertEquals("000001", value1.fundCode());
        assertEquals("000002", value2.fundCode());
    }

    @Test
    void testBaseFactorValueWithDifferentFactors() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue navValue = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("1.2345"), updatedAt
        );

        BaseFactorValue feeValue = new BaseFactorValue(
                "bfv-002", "000001", "bf-002", dataDate, new BigDecimal("0.0150"), updatedAt
        );

        assertEquals("bf-001", navValue.baseFactorId());
        assertEquals("bf-002", feeValue.baseFactorId());
    }

    @Test
    void testBaseFactorValueWithDifferentDates() {
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value1 = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", LocalDate.of(2024, 1, 1),
                new BigDecimal("1.0000"), updatedAt
        );

        BaseFactorValue value2 = new BaseFactorValue(
                "bfv-002", "000001", "bf-001", LocalDate.of(2024, 1, 2),
                new BigDecimal("1.0001"), updatedAt
        );

        assertEquals(LocalDate.of(2024, 1, 1), value1.dataDate());
        assertEquals(LocalDate.of(2024, 1, 2), value2.dataDate());
    }

    @Test
    void testBaseFactorValueEquality() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value1 = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("1.2345"), updatedAt
        );

        BaseFactorValue value2 = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("1.2345"), updatedAt
        );

        assertEquals(value1, value2);
        assertEquals(value1.hashCode(), value2.hashCode());
    }

    @Test
    void testBaseFactorValueToString() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("1.2345"), updatedAt
        );

        String valueString = value.toString();
        assertNotNull(valueString);
        assertTrue(valueString.contains("bfv-001"));
        assertTrue(valueString.contains("000001"));
    }

    @Test
    void testBaseFactorValueWithZeroValue() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, BigDecimal.ZERO, updatedAt
        );

        assertEquals(BigDecimal.ZERO, value.value());
    }

    @Test
    void testBaseFactorValueWithLargeValue() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        BigDecimal largeValue = new BigDecimal("999999.9999");

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, largeValue, updatedAt
        );

        assertEquals(largeValue, value.value());
    }

    @Test
    void testBaseFactorValueWithNegativeValue() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate, new BigDecimal("-0.0150"), updatedAt
        );

        assertEquals(new BigDecimal("-0.0150"), value.value());
    }

    @Test
    void testBaseFactorValueWithPrecision() {
        LocalDate dataDate = LocalDate.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        BaseFactorValue value = new BaseFactorValue(
                "bfv-001", "000001", "bf-001", dataDate,
                new BigDecimal("0.123456789"), updatedAt
        );

        assertEquals(new BigDecimal("0.123456789"), value.value());
    }
}