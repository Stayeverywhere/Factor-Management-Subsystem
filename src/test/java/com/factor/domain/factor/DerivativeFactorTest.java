package com.factor.domain.factor;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DerivedFactorTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updated = now.plusHours(1);
        DerivedFactor factor = new DerivedFactor(
                "df-001",
                "市盈率",
                "formula-001",
                new BigDecimal("15.5"),
                "倍",
                "ACTIVE",
                now,
                updated
        );

        assertEquals("df-001", factor.id());
        assertEquals("市盈率", factor.name());
        assertEquals("formula-001", factor.formulaId());
        assertEquals(new BigDecimal("15.5"), factor.value());
        assertEquals("倍", factor.unit());
        assertEquals("ACTIVE", factor.status());
        assertEquals(now, factor.createdAt());
        assertEquals(updated, factor.updatedAt());
    }

    @Test
    void testNullValues() {
        DerivedFactor factor = new DerivedFactor(
                null, null, null, null, null, null, null, null
        );

        assertNull(factor.id());
        assertNull(factor.name());
        assertNull(factor.formulaId());
        assertNull(factor.value());
        assertNull(factor.unit());
        assertNull(factor.status());
        assertNull(factor.createdAt());
        assertNull(factor.updatedAt());
    }

    @Test
    void testRecordEquality() {
        LocalDateTime now = LocalDateTime.now();
        DerivedFactor factor1 = new DerivedFactor(
                "df-001", "市盈率", "formula-001", new BigDecimal("15.5"),
                "倍", "ACTIVE", now, now
        );
        DerivedFactor factor2 = new DerivedFactor(
                "df-001", "市盈率", "formula-001", new BigDecimal("15.5"),
                "倍", "ACTIVE", now, now
        );

        assertEquals(factor1, factor2);
        assertEquals(factor1.hashCode(), factor2.hashCode());
    }

    @Test
    void testRecordInequality() {
        LocalDateTime now = LocalDateTime.now();
        DerivedFactor factor1 = new DerivedFactor(
                "df-001", "市盈率", "formula-001", new BigDecimal("15.5"),
                "倍", "ACTIVE", now, now
        );
        DerivedFactor factor2 = new DerivedFactor(
                "df-002", "市净率", "formula-002", new BigDecimal("2.3"),
                "倍", "ACTIVE", now, now
        );

        assertNotEquals(factor1, factor2);
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        DerivedFactor factor = new DerivedFactor(
                "df-001", "市盈率", "formula-001", new BigDecimal("15.5"),
                "倍", "ACTIVE", now, now
        );

        String str = factor.toString();
        assertTrue(str.contains("df-001"));
        assertTrue(str.contains("市盈率"));
        assertTrue(str.contains("15.5"));
    }
}