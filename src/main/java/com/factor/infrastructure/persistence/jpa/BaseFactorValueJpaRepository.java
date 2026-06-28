package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseFactorValueJpaRepository extends JpaRepository<BaseFactorValueEntity, String> {
    List<BaseFactorValueEntity> findByFundCodeAndBaseFactorId(String fundCode, String baseFactorId, Pageable pageable);
    List<BaseFactorValueEntity> findByBaseFactorId(String baseFactorId, Pageable pageable);
}
