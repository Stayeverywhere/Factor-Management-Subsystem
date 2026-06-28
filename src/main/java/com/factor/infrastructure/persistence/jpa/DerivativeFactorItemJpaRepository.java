package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DerivativeFactorItemJpaRepository extends JpaRepository<DerivativeFactorItemEntity, String> {
    List<DerivativeFactorItemEntity> findByDerivativeFactorId(String derivativeFactorId);
}
