package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseFactorJpaRepository extends JpaRepository<BaseFactorEntity, String> {
    List<BaseFactorEntity> findByCategoryId(String categoryId);
}
