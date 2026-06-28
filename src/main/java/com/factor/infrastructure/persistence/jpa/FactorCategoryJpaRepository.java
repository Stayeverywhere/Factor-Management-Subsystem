package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactorCategoryJpaRepository extends JpaRepository<FactorCategoryEntity, String> {
    List<FactorCategoryEntity> findByParentId(String parentId);
    List<FactorCategoryEntity> findByParentIdIsNull();
}
