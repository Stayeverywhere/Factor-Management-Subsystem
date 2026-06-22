package com.factor.domain.factor.repository;

import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;

import java.util.List;
import java.util.Optional;

public interface FactorRepository {
    Optional<Factor> findById(String id);

    List<Factor> findAll();

    List<Factor> findByCategory(FactorCategory category);

    Factor save(Factor factor);
}
