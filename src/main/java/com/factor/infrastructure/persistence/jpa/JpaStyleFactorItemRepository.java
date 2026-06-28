package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.StyleFactorItem;
import com.factor.domain.factor.repository.StyleFactorItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaStyleFactorItemRepository implements StyleFactorItemRepository {

    private final StyleFactorItemJpaRepository jpa;

    public JpaStyleFactorItemRepository(StyleFactorItemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<StyleFactorItem> findByStyleFactorId(String styleFactorId) {
        return jpa.findByStyleFactorId(styleFactorId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<StyleFactorItem> saveAll(List<StyleFactorItem> items) {
        List<StyleFactorItemEntity> entities = items.stream()
                .map(item -> {
                    StyleFactorItemEntity e = toEntity(item);
                    if (e.getId() == null) e.setId(UUID.randomUUID().toString());
                    return e;
                }).toList();
        return jpa.saveAll(entities).stream().map(this::toDomain).toList();
    }

    private StyleFactorItem toDomain(StyleFactorItemEntity e) {
        return new StyleFactorItem(e.getId(), e.getStyleFactorId(),
                e.getDerivativeFactorId(), e.getWeight());
    }

    private StyleFactorItemEntity toEntity(StyleFactorItem item) {
        StyleFactorItemEntity e = new StyleFactorItemEntity();
        e.setId(item.id()); e.setStyleFactorId(item.styleFactorId());
        e.setDerivativeFactorId(item.derivativeFactorId()); e.setWeight(item.weight());
        return e;
    }
}
