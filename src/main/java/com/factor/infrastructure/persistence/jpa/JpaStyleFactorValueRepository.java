package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.StyleFactorValue;
import com.factor.domain.factor.repository.StyleFactorValueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaStyleFactorValueRepository implements StyleFactorValueRepository {

    private final StyleFactorValueJpaRepository jpa;

    public JpaStyleFactorValueRepository(StyleFactorValueJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<StyleFactorValue> query(String fundCode, String factorId) {
        PageRequest limit = PageRequest.of(0, 31);
        List<StyleFactorValueEntity> entities;
        if (fundCode != null && !fundCode.isBlank()) {
            entities = jpa.findByFundCodeAndStyleFactorId(fundCode, factorId, limit);
        } else {
            entities = jpa.findByStyleFactorId(factorId, limit);
        }
        return entities.stream().map(this::toDomain).toList();
    }

    public StyleFactorValue save(StyleFactorValue value) {
        StyleFactorValueEntity entity = toEntity(value);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        if (entity.getCalculatedAt() == null) entity.setCalculatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private StyleFactorValue toDomain(StyleFactorValueEntity e) {
        return new StyleFactorValue(e.getId(), e.getFundCode(),
                e.getStyleFactorId(), e.getDataDate(), e.getValue(), e.getCalculatedAt());
    }

    private StyleFactorValueEntity toEntity(StyleFactorValue v) {
        StyleFactorValueEntity e = new StyleFactorValueEntity();
        e.setId(v.id()); e.setFundCode(v.fundCode());
        e.setStyleFactorId(v.styleFactorId());
        e.setDataDate(v.dataDate()); e.setValue(v.value());
        return e;
    }
}
