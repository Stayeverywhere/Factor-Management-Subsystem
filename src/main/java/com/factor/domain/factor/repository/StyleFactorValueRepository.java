package com.factor.domain.factor.repository;

import com.factor.domain.factor.StyleFactorValue;

import java.time.LocalDate;
import java.util.List;

public interface StyleFactorValueRepository {
    List<StyleFactorValue> query(String fundCode, String factorId);
    default List<StyleFactorValue> query(String fundCode, String factorId, LocalDate startDate, LocalDate endDate) {
        return query(fundCode, factorId);
    }
}
