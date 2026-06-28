package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.DerivativeFactorItem;
import com.factor.domain.factor.repository.DerivativeFactorItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaDerivativeFactorItemRepository implements DerivativeFactorItemRepository {

    private final DerivativeFactorItemJpaRepository jpa;

    public JpaDerivativeFactorItemRepository(DerivativeFactorItemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<DerivativeFactorItem> findByDerivativeFactorId(String derivativeFactorId) {
        return jpa.findByDerivativeFactorId(derivativeFactorId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<DerivativeFactorItem> saveAll(List<DerivativeFactorItem> items) {
        List<DerivativeFactorItemEntity> entities = items.stream()
                .map(item -> {
                    DerivativeFactorItemEntity e = toEntity(item);
                    if (e.getId() == null) e.setId(UUID.randomUUID().toString());
                    return e;
                }).toList();
        return jpa.saveAll(entities).stream().map(this::toDomain).toList();
    }

    private DerivativeFactorItem toDomain(DerivativeFactorItemEntity e) {
        return new DerivativeFactorItem(e.getId(), e.getDerivativeFactorId(),
                e.getBaseFactorId(), e.getWeight());
    }

    private DerivativeFactorItemEntity toEntity(DerivativeFactorItem item) {
        DerivativeFactorItemEntity e = new DerivativeFactorItemEntity();
        e.setId(item.id()); e.setDerivativeFactorId(item.derivativeFactorId());
        e.setBaseFactorId(item.baseFactorId()); e.setWeight(item.weight());
        return e;
    }
}
