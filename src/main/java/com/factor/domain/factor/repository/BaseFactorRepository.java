package com.factor.domain.factor.repository;

import com.factor.domain.factor.BaseFactor;

import java.util.List;
import java.util.Optional;

public interface BaseFactorRepository {
    List<BaseFactor> findAll();
    Optional<BaseFactor> findById(String id);
    BaseFactor save(BaseFactor factor);
}
