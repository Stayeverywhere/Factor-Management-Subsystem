package com.factor.domain.factor.repository;

import com.factor.domain.factor.FundInfo;

import java.util.List;
import java.util.Optional;

public interface FundInfoRepository {
    List<FundInfo> findAll();
    Optional<FundInfo> findByCode(String fundCode);
}
