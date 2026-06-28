package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.StyleFactorDefinition;
import com.factor.domain.factor.repository.StyleFactorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaStyleFactorRepository implements StyleFactorRepository {

    private final StyleFactorJpaRepository jpa;

    public JpaStyleFactorRepository(StyleFactorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<StyleFactorDefinition> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<StyleFactorDefinition> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public StyleFactorDefinition save(StyleFactorDefinition factor) {
        StyleFactorEntity entity = toEntity(factor);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        if (entity.getCreatedAt() == null) entity.setCreatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private StyleFactorDefinition toDomain(StyleFactorEntity e) {
        return new StyleFactorDefinition(e.getId(), e.getName(),
                e.getCreatedBy(), e.getCreatedAt(), e.getDescription(), e.isEnabled());
    }

    private StyleFactorEntity toEntity(StyleFactorDefinition f) {
        StyleFactorEntity e = new StyleFactorEntity();
        e.setId(f.id()); e.setName(f.name()); e.setCreatedBy(f.createdBy());
        e.setCreatedAt(f.createdAt()); e.setDescription(f.description());
        e.setEnabled(f.enabled());
        return e;
    }
}
