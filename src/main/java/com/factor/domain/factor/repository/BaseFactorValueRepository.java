package com.factor.domain.factor.repository;

import com.factor.domain.factor.BaseFactorValue;

import java.util.List;

public interface BaseFactorValueRepository {
    List<BaseFactorValue> query(String fundCode, String factorId);
}
