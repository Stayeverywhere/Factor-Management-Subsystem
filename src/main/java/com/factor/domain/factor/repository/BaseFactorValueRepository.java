package com.factor.domain.factor.repository;

import com.factor.domain.factor.BaseFactorValue;

import java.time.LocalDate;
import java.util.List;

public interface BaseFactorValueRepository {
    List<BaseFactorValue> query(String fundCode, String factorId);
    default List<BaseFactorValue> query(String fundCode, String factorId, LocalDate startDate, LocalDate endDate) {
        return query(fundCode, factorId);
    }
}
