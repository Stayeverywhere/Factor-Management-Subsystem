package com.factor.domain.factor.repository;

import com.factor.domain.factor.FactorCategoryNode;

import java.util.List;
import java.util.Optional;

public interface FactorCategoryRepository {
    List<FactorCategoryNode> findTree();
    Optional<FactorCategoryNode> findById(String id);
    FactorCategoryNode save(FactorCategoryNode category);
}
