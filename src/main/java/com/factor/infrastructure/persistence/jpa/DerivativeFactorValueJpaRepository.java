package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DerivativeFactorValueJpaRepository extends JpaRepository<DerivativeFactorValueEntity, String> {
    List<DerivativeFactorValueEntity> findByFundCodeAndDerivativeFactorId(String fundCode, String derivativeFactorId, Pageable pageable);
    List<DerivativeFactorValueEntity> findByDerivativeFactorId(String derivativeFactorId, Pageable pageable);
}
