package com.factor.domain.factor.repository;

import com.factor.domain.factor.DerivativeFactorValue;

import java.time.LocalDate;
import java.util.List;

public interface DerivativeFactorValueRepository {
    List<DerivativeFactorValue> query(String fundCode, String factorId);
    default List<DerivativeFactorValue> query(String fundCode, String factorId, LocalDate startDate, LocalDate endDate) {
        return query(fundCode, factorId);
    }
}
