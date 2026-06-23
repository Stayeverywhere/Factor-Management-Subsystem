package com.factor.domain.factor.repository;

import com.factor.domain.factor.DerivativeFactor;

import java.util.List;
import java.util.Optional;

public interface DerivativeFactorRepository {
    List<DerivativeFactor> findAll();
    Optional<DerivativeFactor> findById(String id);
    DerivativeFactor save(DerivativeFactor factor);
}
