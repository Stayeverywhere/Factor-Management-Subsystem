package com.factor.infrastructure.persistence;

import com.factor.domain.factor.BaseFactorValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础因子值仓储内存实现测试
 */
class InMemoryBaseFactorValueRepositoryTest {

    private InMemoryBaseFactorValueRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBaseFactorValueRepository();
    }

    @Test
    void testQueryExistingFundAndFactor() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");
        assertNotNull(values);
        assertTrue(values.size() > 0);
        values.forEach(value -> {
            assertEquals("000001", value.fundCode());
            assertEquals("bf-1", value.baseFactorId());
        });
    }

    @Test
    void testQueryNotFound() {
        List<BaseFactorValue> values = repository.query("999999", "bf-999");
        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    void testQueryMultipleDates() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");
        assertTrue(values.size() >= 3);

        LocalDate date1 = values.get(0).dataDate();
        LocalDate date2 = values.get(1).dataDate();
        LocalDate date3 = values.get(2).dataDate();

        assertNotNull(date1);
        assertNotNull(date2);
        assertNotNull(date3);
    }

    @Test
    void testQueryValuesInRange() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");

        for (BaseFactorValue value : values) {
            assertNotNull(value.value());
            assertTrue(value.value().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Test
    void testQueryReturnsImmutableList() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");
        assertNotNull(values);

        LocalDateTime now = LocalDateTime.now();
        assertThrows(UnsupportedOperationException.class, () -> {
            values.add(new BaseFactorValue(
                    "test-id", "000001", "bf-1", LocalDate.now(),
                    BigDecimal.ONE, now
            ));
        });
    }

    @Test
    void testQueryByFundCode() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");

        values.forEach(value -> assertEquals("000001", value.fundCode()));
    }

    @Test
    void testQueryByFactorId() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");

        values.forEach(value -> assertEquals("bf-1", value.baseFactorId()));
    }

    @Test
    void testQueryEmptyFundCode() {
        List<BaseFactorValue> values = repository.query("", "bf-1");
        assertEquals(0, values.size());
    }

    @Test
    void testQueryEmptyFactorId() {
        List<BaseFactorValue> values = repository.query("000001", "");
        assertEquals(0, values.size());
    }

    @Test
    void testQueryValueTimestamps() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");

        for (BaseFactorValue value : values) {
            assertNotNull(value.updatedAt());
        }
    }

    @Test
    void testQueryMultipleValues() {
        List<BaseFactorValue> values = repository.query("000001", "bf-1");

        assertTrue(values.size() > 0);

        BigDecimal previousValue = null;
        for (BaseFactorValue value : values) {
            assertNotNull(value.value());
            if (previousValue != null) {
                assertNotEquals(previousValue, value.value());
            }
            previousValue = value.value();
        }
    }

    @Test
    void testQueryFundNotFound() {
        List<BaseFactorValue> values = repository.query("999999", "bf-1");
        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    void testQueryFactorNotFound() {
        List<BaseFactorValue> values = repository.query("000001", "bf-999");
        assertNotNull(values);
        assertEquals(0, values.size());
    }
}