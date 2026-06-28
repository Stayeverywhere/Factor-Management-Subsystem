package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.DerivativeFactor;
import com.factor.domain.factor.repository.DerivativeFactorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaDerivativeFactorRepository implements DerivativeFactorRepository {

    private final DerivativeFactorJpaRepository jpa;

    public JpaDerivativeFactorRepository(DerivativeFactorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<DerivativeFactor> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<DerivativeFactor> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public DerivativeFactor save(DerivativeFactor factor) {
        DerivativeFactorEntity entity = toEntity(factor);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        if (entity.getCreatedAt() == null) entity.setCreatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private DerivativeFactor toDomain(DerivativeFactorEntity e) {
        return new DerivativeFactor(e.getId(), e.getCode(), e.getName(),
                e.getCreatedBy(), e.getCreatedAt(), e.getDescription(), e.getFormula(), e.isEnabled());
    }

    private DerivativeFactorEntity toEntity(DerivativeFactor f) {
        DerivativeFactorEntity e = new DerivativeFactorEntity();
        e.setId(f.id()); e.setCode(f.code()); e.setName(f.name());
        e.setCreatedBy(f.createdBy()); e.setCreatedAt(f.createdAt());
        e.setDescription(f.description()); e.setFormula(f.formula());
        e.setEnabled(f.enabled());
        return e;
    }
}
