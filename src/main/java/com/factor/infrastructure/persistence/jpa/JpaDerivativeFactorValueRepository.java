package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.DerivativeFactorValue;
import com.factor.domain.factor.repository.DerivativeFactorValueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaDerivativeFactorValueRepository implements DerivativeFactorValueRepository {

    private final DerivativeFactorValueJpaRepository jpa;

    public JpaDerivativeFactorValueRepository(DerivativeFactorValueJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<DerivativeFactorValue> query(String fundCode, String factorId) {
        PageRequest limit = PageRequest.of(0, 31);
        List<DerivativeFactorValueEntity> entities;
        if (fundCode != null && !fundCode.isBlank()) {
            entities = jpa.findByFundCodeAndDerivativeFactorId(fundCode, factorId, limit);
        } else {
            entities = jpa.findByDerivativeFactorId(factorId, limit);
        }
        return entities.stream().map(this::toDomain).toList();
    }

    public DerivativeFactorValue save(DerivativeFactorValue value) {
        DerivativeFactorValueEntity entity = toEntity(value);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        if (entity.getCalculatedAt() == null) entity.setCalculatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private DerivativeFactorValue toDomain(DerivativeFactorValueEntity e) {
        return new DerivativeFactorValue(e.getId(), e.getFundCode(),
                e.getDerivativeFactorId(), e.getDataDate(), e.getValue(), e.getCalculatedAt());
    }

    private DerivativeFactorValueEntity toEntity(DerivativeFactorValue v) {
        DerivativeFactorValueEntity e = new DerivativeFactorValueEntity();
        e.setId(v.id()); e.setFundCode(v.fundCode());
        e.setDerivativeFactorId(v.derivativeFactorId());
        e.setDataDate(v.dataDate()); e.setValue(v.value());
        return e;
    }
}
