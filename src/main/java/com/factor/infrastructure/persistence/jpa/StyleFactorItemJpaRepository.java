package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleFactorItemJpaRepository extends JpaRepository<StyleFactorItemEntity, String> {
    List<StyleFactorItemEntity> findByStyleFactorId(String styleFactorId);
}
