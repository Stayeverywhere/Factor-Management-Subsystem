package com.factor.domain.factor;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基金信息领域模型测试
 */
class FundInfoTest {

    @Test
    void testCreateFundInfo() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fundInfo = new FundInfo(
                "000001",
                "易方达天天理财货币A",
                "天天理财A",
                "货币型",
                establishmentDate,
                "易方达基金",
                "张经理",
                "OPEN"
        );

        assertEquals("000001", fundInfo.fundCode());
        assertEquals("易方达天天理财货币A", fundInfo.fundName());
        assertEquals("天天理财A", fundInfo.fundShortName());
        assertEquals("货币型", fundInfo.fundType());
        assertEquals(establishmentDate, fundInfo.establishmentDate());
        assertEquals("易方达基金", fundInfo.issuer());
        assertEquals("张经理", fundInfo.fundManager());
        assertEquals("OPEN", fundInfo.status());
    }

    @Test
    void testFundInfoDifferentTypes() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo moneyFund = new FundInfo(
                "000001", "货币基金A", "货币A", "货币型",
                establishmentDate, "基金公司A", "经理A", "OPEN"
        );

        FundInfo mixedFund = new FundInfo(
                "000002", "混合基金B", "混合B", "混合型",
                establishmentDate, "基金公司B", "经理B", "OPEN"
        );

        FundInfo stockFund = new FundInfo(
                "000003", "股票基金C", "股票C", "股票型",
                establishmentDate, "基金公司C", "经理C", "OPEN"
        );

        assertEquals("货币型", moneyFund.fundType());
        assertEquals("混合型", mixedFund.fundType());
        assertEquals("股票型", stockFund.fundType());
    }

    @Test
    void testFundInfoDifferentStatuses() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo openFund = new FundInfo(
                "000001", "基金A", "基金A", "货币型",
                establishmentDate, "基金公司", "经理", "OPEN"
        );

        FundInfo closedFund = new FundInfo(
                "000002", "基金B", "基金B", "混合型",
                establishmentDate, "基金公司", "经理", "CLOSED"
        );

        assertEquals("OPEN", openFund.status());
        assertEquals("CLOSED", closedFund.status());
    }

    @Test
    void testFundInfoEquality() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fundInfo1 = new FundInfo(
                "000001", "易方达天天理财货币A", "天天理财A", "货币型",
                establishmentDate, "易方达基金", "张经理", "OPEN"
        );

        FundInfo fundInfo2 = new FundInfo(
                "000001", "易方达天天理财货币A", "天天理财A", "货币型",
                establishmentDate, "易方达基金", "张经理", "OPEN"
        );

        assertEquals(fundInfo1, fundInfo2);
        assertEquals(fundInfo1.hashCode(), fundInfo2.hashCode());
    }

    @Test
    void testFundInfoToString() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fundInfo = new FundInfo(
                "000001", "易方达天天理财货币A", "天天理财A", "货币型",
                establishmentDate, "易方达基金", "张经理", "OPEN"
        );

        String fundInfoString = fundInfo.toString();
        assertNotNull(fundInfoString);
        assertTrue(fundInfoString.contains("000001"));
        assertTrue(fundInfoString.contains("易方达天天理财货币A"));
    }

    @Test
    void testFundInfoEstablishmentDate() {
        LocalDate date1 = LocalDate.of(2010, 1, 1);
        LocalDate date2 = LocalDate.of(2020, 6, 15);

        FundInfo oldFund = new FundInfo(
                "000001", "老基金", "老基金", "货币型",
                date1, "基金公司", "经理", "OPEN"
        );

        FundInfo newFund = new FundInfo(
                "000002", "新基金", "新基金", "混合型",
                date2, "基金公司", "经理", "OPEN"
        );

        assertEquals(date1, oldFund.establishmentDate());
        assertEquals(date2, newFund.establishmentDate());
    }

    @Test
    void testFundInfoDifferentIssuers() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fund1 = new FundInfo(
                "000001", "基金1", "基金1", "货币型",
                establishmentDate, "易方达基金", "经理1", "OPEN"
        );

        FundInfo fund2 = new FundInfo(
                "000002", "基金2", "基金2", "混合型",
                establishmentDate, "中欧基金", "经理2", "OPEN"
        );

        assertEquals("易方达基金", fund1.issuer());
        assertEquals("中欧基金", fund2.issuer());
    }

    @Test
    void testFundInfoDifferentManagers() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fund1 = new FundInfo(
                "000001", "基金1", "基金1", "货币型",
                establishmentDate, "基金公司", "张经理", "OPEN"
        );

        FundInfo fund2 = new FundInfo(
                "000002", "基金2", "基金2", "混合型",
                establishmentDate, "基金公司", "李经理", "OPEN"
        );

        assertEquals("张经理", fund1.fundManager());
        assertEquals("李经理", fund2.fundManager());
    }

    @Test
    void testFundInfoWithShortName() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fundWithShortName = new FundInfo(
                "000001", "易方达天天理财货币市场基金A类", "天天理财A",
                "货币型", establishmentDate, "易方达基金", "张经理", "OPEN"
        );

        assertEquals("易方达天天理财货币市场基金A类", fundWithShortName.fundName());
        assertEquals("天天理财A", fundWithShortName.fundShortName());
    }

    @Test
    void testFundInfoCodeFormat() {
        LocalDate establishmentDate = LocalDate.of(2020, 1, 1);

        FundInfo fund1 = new FundInfo(
                "000001", "基金1", "基金1", "货币型",
                establishmentDate, "基金公司", "经理", "OPEN"
        );

        FundInfo fund2 = new FundInfo(
                "110022", "基金2", "基金2", "混合型",
                establishmentDate, "基金公司", "经理", "OPEN"
        );

        assertEquals("000001", fund1.fundCode());
        assertEquals("110022", fund2.fundCode());
    }
}