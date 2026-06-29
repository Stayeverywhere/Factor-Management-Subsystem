package com.factor.infrastructure.persistence;

import com.factor.domain.factor.StyleFactorDefinition;
import com.factor.domain.factor.repository.StyleFactorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryStyleFactorRepository implements StyleFactorRepository {

    private final List<StyleFactorDefinition> storage = new ArrayList<>(List.of(
            new StyleFactorDefinition("sf-1", "稳健风格因子", "system", LocalDateTime.now(), "稳健收益风格", true)
    ));

    @Override
    public List<StyleFactorDefinition> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<StyleFactorDefinition> findById(String id) {
        return storage.stream().filter(item -> item.id().equals(id)).findFirst();
    }

    @Override
    public StyleFactorDefinition save(StyleFactorDefinition factor) {
        StyleFactorDefinition stored = factor.id() == null ? new StyleFactorDefinition(UUID.randomUUID().toString(), factor.name(), factor.createdBy(), factor.createdAt(), factor.description(), factor.enabled()) : factor;
        storage.removeIf(item -> item.id().equals(stored.id()));
        storage.add(stored);
        return stored;
    }
}
