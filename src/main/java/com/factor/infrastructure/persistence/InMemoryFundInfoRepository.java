package com.factor.infrastructure.persistence;

import com.factor.domain.factor.FundInfo;
import com.factor.domain.factor.repository.FundInfoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryFundInfoRepository implements FundInfoRepository {

    private final List<FundInfo> storage = List.of(
            new FundInfo("000001", "易方达天天理财货币A", "天天理财A", "货币型", LocalDate.of(2010, 1, 1), "易方达基金", "张经理", "OPEN"),
            new FundInfo("110022", "中欧医疗健康混合A", "医疗健康A", "混合型", LocalDate.of(2018, 6, 1), "中欧基金", "李经理", "OPEN")
    );

    @Override
    public List<FundInfo> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<FundInfo> findByCode(String fundCode) {
        return storage.stream().filter(item -> item.fundCode().equals(fundCode)).findFirst();
    }
}
