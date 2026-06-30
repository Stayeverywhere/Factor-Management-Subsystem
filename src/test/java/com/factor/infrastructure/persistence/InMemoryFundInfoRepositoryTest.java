package com.factor.infrastructure.persistence;

import com.factor.domain.factor.FundInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基金信息仓储内存实现测试
 */
class InMemoryFundInfoRepositoryTest {

    private InMemoryFundInfoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFundInfoRepository();
    }

    @Test
    void testFindAll() {
        List<FundInfo> funds = repository.findAll();
        assertNotNull(funds);
        assertTrue(funds.size() > 0);
    }

    @Test
    void testFindByCodeExisting() {
        Optional<FundInfo> fund = repository.findByCode("000001");
        assertTrue(fund.isPresent());
        assertEquals("000001", fund.get().fundCode());
        assertEquals("易方达天天理财货币A", fund.get().fundName());
        assertEquals("天天理财A", fund.get().fundShortName());
    }

    @Test
    void testFindByCodeNotFound() {
        Optional<FundInfo> fund = repository.findByCode("999999");
        assertFalse(fund.isPresent());
    }

    @Test
    void testFindAllReturnsImmutableList() {
        List<FundInfo> funds = repository.findAll();
        assertNotNull(funds);

        assertThrows(UnsupportedOperationException.class, () -> {
            funds.add(new FundInfo(
                    "999999", "测试基金", "测试", "货币型",
                    LocalDate.now(), "测试公司", "测试经理", "OPEN"
            ));
        });
    }

    @Test
    void testFindByCodeMultiple() {
        Optional<FundInfo> fund1 = repository.findByCode("000001");
        Optional<FundInfo> fund2 = repository.findByCode("110022");

        assertTrue(fund1.isPresent());
        assertTrue(fund2.isPresent());

        assertEquals("易方达天天理财货币A", fund1.get().fundName());
        assertEquals("中欧医疗健康混合A", fund2.get().fundName());
    }

    @Test
    void testFundInfoData() {
        Optional<FundInfo> fund = repository.findByCode("000001");
        assertTrue(fund.isPresent());

        FundInfo fundInfo = fund.get();
        assertEquals("货币型", fundInfo.fundType());
        assertEquals("易方达基金", fundInfo.issuer());
        assertEquals("张经理", fundInfo.fundManager());
        assertEquals("OPEN", fundInfo.status());
        assertNotNull(fundInfo.establishmentDate());
    }

    @Test
    void testFindByCodeWithDifferentTypes() {
        Optional<FundInfo> moneyFund = repository.findByCode("000001");
        Optional<FundInfo> mixedFund = repository.findByCode("110022");

        assertTrue(moneyFund.isPresent());
        assertTrue(mixedFund.isPresent());

        assertEquals("货币型", moneyFund.get().fundType());
        assertEquals("混合型", mixedFund.get().fundType());
    }

    @Test
    void testFundInfoEstablishmentDates() {
        Optional<FundInfo> fund1 = repository.findByCode("000001");
        Optional<FundInfo> fund2 = repository.findByCode("110022");

        assertTrue(fund1.isPresent());
        assertTrue(fund2.isPresent());

        LocalDate date1 = fund1.get().establishmentDate();
        LocalDate date2 = fund2.get().establishmentDate();

        assertNotNull(date1);
        assertNotNull(date2);
    }

    @Test
    void testFundInfoIssuers() {
        Optional<FundInfo> fund1 = repository.findByCode("000001");
        Optional<FundInfo> fund2 = repository.findByCode("110022");

        assertTrue(fund1.isPresent());
        assertTrue(fund2.isPresent());

        assertEquals("易方达基金", fund1.get().issuer());
        assertEquals("中欧基金", fund2.get().issuer());
    }

    @Test
    void testFindByCodeEmpty() {
        Optional<FundInfo> fund = repository.findByCode("");
        assertFalse(fund.isPresent());
    }

    @Test
    void testFindByCodeNull() {
        Optional<FundInfo> fund = repository.findByCode(null);
        assertFalse(fund.isPresent());
    }

    @Test
    void testAllFundsHaveFundCode() {
        List<FundInfo> funds = repository.findAll();

        for (FundInfo fund : funds) {
            assertNotNull(fund.fundCode());
            assertTrue(fund.fundCode().length() > 0);
        }
    }

    @Test
    void testAllFundsHaveFundName() {
        List<FundInfo> funds = repository.findAll();

        for (FundInfo fund : funds) {
            assertNotNull(fund.fundName());
            assertTrue(fund.fundName().length() > 0);
        }
    }

    @Test
    void testAllFundsHaveStatus() {
        List<FundInfo> funds = repository.findAll();

        for (FundInfo fund : funds) {
            assertNotNull(fund.status());
            assertTrue(fund.status().length() > 0);
        }
    }
}