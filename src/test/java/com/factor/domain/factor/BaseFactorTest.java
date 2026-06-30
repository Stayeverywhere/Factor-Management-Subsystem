package com.factor.domain.factor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础因子领域模型测试
 */
class BaseFactorTest {

    @Test
    void testCreateBaseFactor() {
        BaseFactor factor = new BaseFactor(
                "bf-001",
                "nav",
                "单位净值",
                "cat-001",
                "数值型",
                "元",
                "日频",
                "Wind",
                "每日净值",
                true,
                true,
                "单位净值因子"
        );

        assertEquals("bf-001", factor.id());
        assertEquals("nav", factor.code());
        assertEquals("单位净值", factor.name());
        assertEquals("cat-001", factor.categoryId());
        assertEquals("数值型", factor.dataType());
        assertEquals("元", factor.unit());
        assertEquals("日频", factor.updateFrequency());
        assertEquals("Wind", factor.dataSource());
        assertEquals("每日净值", factor.fetchLogic());
        assertTrue(factor.enabled());
        assertTrue(factor.derivable());
        assertEquals("单位净值因子", factor.description());
    }

    @Test
    void testBaseFactorWithNullId() {
        BaseFactor factor = new BaseFactor(
                null,
                "nav",
                "单位净值",
                "cat-001",
                "数值型",
                "元",
                "日频",
                "Wind",
                "每日净值",
                true,
                true,
                "单位净值因子"
        );

        assertNull(factor.id());
        assertEquals("nav", factor.code());
    }

    @Test
    void testBaseFactorEnabledStates() {
        BaseFactor enabledFactor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "单位净值因子"
        );

        BaseFactor disabledFactor = new BaseFactor(
                "bf-002", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", false, true, "单位净值因子"
        );

        assertTrue(enabledFactor.enabled());
        assertFalse(disabledFactor.enabled());
    }

    @Test
    void testBaseFactorDerivableStates() {
        BaseFactor derivableFactor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "单位净值因子"
        );

        BaseFactor nonDerivableFactor = new BaseFactor(
                "bf-002", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, false, "单位净值因子"
        );

        assertTrue(derivableFactor.derivable());
        assertFalse(nonDerivableFactor.derivable());
    }

    @Test
    void testBaseFactorDataTypes() {
        BaseFactor numericFactor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "数值因子"
        );

        BaseFactor textFactor = new BaseFactor(
                "bf-002", "fund_name", "基金名称", "cat-002", "文本型", null,
                "月度", "公告", "基金公告", true, false, "文本因子"
        );

        assertEquals("数值型", numericFactor.dataType());
        assertEquals("文本型", textFactor.dataType());
    }

    @Test
    void testBaseFactorUpdateFrequency() {
        BaseFactor dailyFactor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "日频因子"
        );

        BaseFactor monthlyFactor = new BaseFactor(
                "bf-002", "management_fee", "管理费率", "cat-001", "数值型", "%",
                "月度", "Wind", "基金公告", true, true, "月度因子"
        );

        assertEquals("日频", dailyFactor.updateFrequency());
        assertEquals("月度", monthlyFactor.updateFrequency());
    }

    @Test
    void testBaseFactorEquality() {
        BaseFactor factor1 = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "单位净值因子"
        );

        BaseFactor factor2 = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "单位净值因子"
        );

        assertEquals(factor1, factor2);
        assertEquals(factor1.hashCode(), factor2.hashCode());
    }

    @Test
    void testBaseFactorToString() {
        BaseFactor factor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "单位净值因子"
        );

        String factorString = factor.toString();
        assertNotNull(factorString);
        assertTrue(factorString.contains("bf-001"));
        assertTrue(factorString.contains("nav"));
        assertTrue(factorString.contains("单位净值"));
    }

    @Test
    void testBaseFactorWithNullUnit() {
        BaseFactor factor = new BaseFactor(
                "bf-001", "fund_name", "基金名称", "cat-002", "文本型", null,
                "月度", "公告", "基金公告", true, false, "文本因子"
        );

        assertNull(factor.unit());
    }

    @Test
    void testBaseFactorWithNullDescription() {
        BaseFactor factor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, null
        );

        assertNull(factor.description());
    }

    @Test
    void testBaseFactorDifferentDataSources() {
        BaseFactor windFactor = new BaseFactor(
                "bf-001", "nav", "单位净值", "cat-001", "数值型", "元",
                "日频", "Wind", "每日净值", true, true, "Wind数据"
        );

        BaseFactor internalFactor = new BaseFactor(
                "bf-002", "custom_factor", "自定义因子", "cat-003", "数值型", "%",
                "日频", "内部系统", "计算公式", true, true, "内部数据"
        );

        assertEquals("Wind", windFactor.dataSource());
        assertEquals("内部系统", internalFactor.dataSource());
    }
}