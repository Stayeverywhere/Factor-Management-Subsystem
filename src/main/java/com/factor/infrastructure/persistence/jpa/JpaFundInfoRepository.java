package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.FundInfo;
import com.factor.domain.factor.repository.FundInfoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaFundInfoRepository implements FundInfoRepository {

    private final FundProfileJpaRepository jpa;

    public JpaFundInfoRepository(FundProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<FundInfo> findAll() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<FundInfo> findByCode(String fundCode) {
        return jpa.findById(fundCode).map(this::toDomain);
    }

    private FundInfo toDomain(FundProfileEntity e) {
        return new FundInfo(
                e.getFundCode(),
                e.getFundName(),
                null,                // fundShortName — DB 无此字段
                e.getFundType(),
                e.getSetupDate(),    // establishmentDate
                e.getCompanyName(),  // issuer
                e.getManagerName(),  // fundManager
                "OPEN"               // status — DB 无此字段，默认开放
        );
    }
}
