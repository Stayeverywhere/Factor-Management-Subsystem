package com.factor.infrastructure.persistence;

import com.factor.domain.factor.DerivativeFactor;
import com.factor.domain.factor.repository.DerivativeFactorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryDerivativeFactorRepository implements DerivativeFactorRepository {

    private final List<DerivativeFactor> storage = new ArrayList<>(List.of(
            new DerivativeFactor("df-1", "fee_bundle", "费率组合因子", "system", LocalDateTime.now(), "管理费率、运作费率、托管费率组合", true)
    ));

    @Override
    public List<DerivativeFactor> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<DerivativeFactor> findById(String id) {
        return storage.stream().filter(item -> item.id().equals(id)).findFirst();
    }

    @Override
    public DerivativeFactor save(DerivativeFactor factor) {
        DerivativeFactor stored = factor.id() == null ? new DerivativeFactor(UUID.randomUUID().toString(), factor.code(), factor.name(), factor.createdBy(), factor.createdAt(), factor.description(), factor.enabled()) : factor;
        storage.removeIf(item -> item.id().equals(stored.id()));
        storage.add(stored);
        return stored;
    }
}
