package com.factor.domain.factor.repository;

import com.factor.domain.factor.DerivativeFactorItem;

import java.util.List;

public interface DerivativeFactorItemRepository {
    List<DerivativeFactorItem> findByDerivativeFactorId(String derivativeFactorId);
    List<DerivativeFactorItem> saveAll(List<DerivativeFactorItem> items);
}
