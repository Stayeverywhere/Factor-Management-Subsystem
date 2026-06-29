package com.factor.infrastructure.persistence;

import com.factor.domain.factor.BaseFactor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础因子仓储内存实现测试
 */
class InMemoryBaseFactorRepositoryTest {

    private InMemoryBaseFactorRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBaseFactorRepository();
    }

    @Test
    void testFindAll() {
        List<BaseFactor> factors = repository.findAll();
        assertNotNull(factors);
        assertTrue(factors.size() > 0);
    }

    @Test
    void testFindByIdExisting() {
        Optional<BaseFactor> factor = repository.findById("bf-1");
        assertTrue(factor.isPresent());
        assertEquals("bf-1", factor.get().id());
        assertEquals("management_fee", factor.get().code());
        assertEquals("管理费率", factor.get().name());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<BaseFactor> factor = repository.findById("non-existent-id");
        assertFalse(factor.isPresent());
    }

    @Test
    void testSaveNewFactor() {
        BaseFactor newFactor = new BaseFactor(
                null,
                "new_factor",
                "新因子",
                "cat-001",
                "数值型",
                "%",
                "日频",
                "Wind",
                "自定义逻辑",
                true,
                true,
                "新因子描述"
        );

        BaseFactor saved = repository.save(newFactor);
        assertNotNull(saved.id());
        assertEquals("new_factor", saved.code());
        assertEquals("新因子", saved.name());

        Optional<BaseFactor> found = repository.findById(saved.id());
        assertTrue(found.isPresent());
    }

    @Test
    void testSaveExistingFactor() {
        BaseFactor existingFactor = new BaseFactor(
                "bf-1",
                "management_fee",
                "管理费率更新",
                "cat-1-1",
                "数值型",
                "%",
                "月度",
                "Wind",
                "基金公告/费率表",
                true,
                true,
                "管理费率更新"
        );

        BaseFactor saved = repository.save(existingFactor);
        assertEquals("bf-1", saved.id());
        assertEquals("管理费率更新", saved.name());
    }

    @Test
    void testSaveMultipleFactors() {
        BaseFactor factor1 = repository.save(new BaseFactor(
                null, "factor1", "因子1", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑1", true, true, "描述1"
        ));

        BaseFactor factor2 = repository.save(new BaseFactor(
                null, "factor2", "因子2", "cat-002", "数值型", "%",
                "月度", "Wind", "逻辑2", true, false, "描述2"
        ));

        List<BaseFactor> allFactors = repository.findAll();
        assertTrue(allFactors.size() >= 2);
    }

    @Test
    void testSaveFactorWithDifferentDataTypes() {
        BaseFactor numericFactor = repository.save(new BaseFactor(
                null, "numeric", "数值因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, true, "数值因子"
        ));

        BaseFactor textFactor = repository.save(new BaseFactor(
                null, "text", "文本因子", "cat-002", "文本型", null,
                "月度", "公告", "公告", true, false, "文本因子"
        ));

        assertEquals("数值型", numericFactor.dataType());
        assertEquals("文本型", textFactor.dataType());
    }

    @Test
    void testSaveFactorWithDisabledState() {
        BaseFactor disabledFactor = repository.save(new BaseFactor(
                null, "disabled", "禁用因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", false, true, "禁用因子"
        ));

        assertNotNull(disabledFactor.id());
        assertFalse(disabledFactor.enabled());
    }

    @Test
    void testSaveFactorWithNonDerivableState() {
        BaseFactor nonDerivableFactor = repository.save(new BaseFactor(
                null, "non_deriv", "不可衍生因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, false, "不可衍生"
        ));

        assertNotNull(nonDerivableFactor.id());
        assertFalse(nonDerivableFactor.derivable());
    }

    @Test
    void testFindByIdAfterUpdate() {
        BaseFactor original = repository.findById("bf-1").orElseThrow();
        String originalName = original.name();

        BaseFactor updated = repository.save(new BaseFactor(
                "bf-1", original.code(), "管理费率修改", original.categoryId(),
                original.dataType(), original.unit(), original.updateFrequency(),
                original.dataSource(), original.fetchLogic(), original.enabled(),
                original.derivable(), "管理费率修改"
        ));

        Optional<BaseFactor> found = repository.findById("bf-1");
        assertTrue(found.isPresent());
        assertEquals("管理费率修改", found.get().name());
    }

    @Test
    void testFindAllReturnsImmutableList() {
        List<BaseFactor> factors = repository.findAll();
        assertNotNull(factors);

        assertThrows(UnsupportedOperationException.class, () -> {
            factors.add(new BaseFactor(
                    "test-id", "test", "测试", "cat", "数值型", "元",
                    "日频", "Wind", "逻辑", true, true, "测试"
            ));
        });
    }

    @Test
    void testRepositoryPersistence() {
        BaseFactor saved1 = repository.save(new BaseFactor(
                null, "persist1", "持久化1", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, true, "持久化测试"
        ));

        Optional<BaseFactor> found = repository.findById(saved1.id());
        assertTrue(found.isPresent());

        BaseFactor saved2 = repository.save(new BaseFactor(
                null, "persist2", "持久化2", "cat-002", "数值型", "%",
                "月度", "Wind", "逻辑", true, true, "持久化测试2"
        ));

        assertTrue(repository.findById(saved1.id()).isPresent());
        assertTrue(repository.findById(saved2.id()).isPresent());
    }

    @Test
    void testSaveFactorWithNullDescription() {
        BaseFactor factor = repository.save(new BaseFactor(
                null, "null_desc", "无描述因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, true, null
        ));

        assertNotNull(factor.id());
        assertNull(factor.description());
    }

    @Test
    void testSaveFactorWithDifferentUpdateFrequency() {
        BaseFactor dailyFactor = repository.save(new BaseFactor(
                null, "daily", "日频因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, true, "日频因子"
        ));

        BaseFactor monthlyFactor = repository.save(new BaseFactor(
                null, "monthly", "月度因子", "cat-002", "数值型", "%",
                "月度", "Wind", "逻辑", true, true, "月度因子"
        ));

        assertEquals("日频", dailyFactor.updateFrequency());
        assertEquals("月度", monthlyFactor.updateFrequency());
    }

    @Test
    void testSaveFactorWithDifferentDataSources() {
        BaseFactor windFactor = repository.save(new BaseFactor(
                null, "wind", "Wind因子", "cat-001", "数值型", "元",
                "日频", "Wind", "逻辑", true, true, "Wind数据源"
        ));

        BaseFactor internalFactor = repository.save(new BaseFactor(
                null, "internal", "内部因子", "cat-002", "数值型", "元",
                "日频", "内部系统", "计算", true, true, "内部数据源"
        ));

        assertEquals("Wind", windFactor.dataSource());
        assertEquals("内部系统", internalFactor.dataSource());
    }
}