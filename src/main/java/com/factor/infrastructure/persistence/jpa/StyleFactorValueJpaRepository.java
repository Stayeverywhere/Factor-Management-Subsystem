package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleFactorValueJpaRepository extends JpaRepository<StyleFactorValueEntity, String> {
    List<StyleFactorValueEntity> findByFundCodeAndStyleFactorId(String fundCode, String styleFactorId, Pageable pageable);
    List<StyleFactorValueEntity> findByStyleFactorId(String styleFactorId, Pageable pageable);
}
