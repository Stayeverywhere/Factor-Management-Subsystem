package com.factor.domain.factor.repository;

import com.factor.domain.factor.StyleFactorValue;

import java.util.List;

public interface StyleFactorValueRepository {
    List<StyleFactorValue> query(String fundCode, String factorId);
}
