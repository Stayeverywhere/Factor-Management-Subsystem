package com.factor.infrastructure.persistence;

import com.factor.domain.factor.DerivativeFactorItem;
import com.factor.domain.factor.repository.DerivativeFactorItemRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryDerivativeFactorItemRepository implements DerivativeFactorItemRepository {

    private final List<DerivativeFactorItem> storage = new ArrayList<>();

    @Override
    public List<DerivativeFactorItem> findByDerivativeFactorId(String derivativeFactorId) {
        return storage.stream().filter(item -> item.derivativeFactorId().equals(derivativeFactorId)).toList();
    }

    @Override
    public List<DerivativeFactorItem> saveAll(List<DerivativeFactorItem> items) {
        storage.addAll(items);
        return List.copyOf(items);
    }
}
