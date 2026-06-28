package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.BaseFactor;
import com.factor.domain.factor.repository.BaseFactorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaBaseFactorRepository implements BaseFactorRepository {

    private final BaseFactorJpaRepository jpa;

    public JpaBaseFactorRepository(BaseFactorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<BaseFactor> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BaseFactor> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public BaseFactor save(BaseFactor factor) {
        BaseFactorEntity entity = toEntity(factor);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private BaseFactor toDomain(BaseFactorEntity e) {
        return new BaseFactor(
                e.getId(), e.getCode(), e.getName(),
                e.getCategoryId(), e.getDataType(), e.getUnit(),
                e.getUpdateFrequency(), e.getDataSource(), e.getFetchLogic(),
                e.isEnabled(), e.isDerivable(), e.getDescription()
        );
    }

    private BaseFactorEntity toEntity(BaseFactor f) {
        BaseFactorEntity e = new BaseFactorEntity();
        e.setId(f.id()); e.setCode(f.code()); e.setName(f.name());
        e.setCategoryId(f.categoryId()); e.setDataType(f.dataType()); e.setUnit(f.unit());
        e.setUpdateFrequency(f.updateFrequency()); e.setDataSource(f.dataSource());
        e.setFetchLogic(f.fetchLogic()); e.setEnabled(f.enabled());
        e.setDerivable(f.derivable()); e.setDescription(f.description());
        return e;
    }
}
