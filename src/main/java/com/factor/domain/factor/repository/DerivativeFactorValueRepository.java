package com.factor.domain.factor.repository;

import com.factor.domain.factor.DerivativeFactorValue;

import java.util.List;

public interface DerivativeFactorValueRepository {
    List<DerivativeFactorValue> query(String fundCode, String factorId);
}
