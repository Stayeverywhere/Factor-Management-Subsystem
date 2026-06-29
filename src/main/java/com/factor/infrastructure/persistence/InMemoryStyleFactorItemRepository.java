package com.factor.infrastructure.persistence;

import com.factor.domain.factor.StyleFactorItem;
import com.factor.domain.factor.repository.StyleFactorItemRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryStyleFactorItemRepository implements StyleFactorItemRepository {

    private final List<StyleFactorItem> storage = new ArrayList<>();

    @Override
    public List<StyleFactorItem> findByStyleFactorId(String styleFactorId) {
        return storage.stream().filter(item -> item.styleFactorId().equals(styleFactorId)).toList();
    }

    @Override
    public List<StyleFactorItem> saveAll(List<StyleFactorItem> items) {
        storage.addAll(items);
        return List.copyOf(items);
    }
}
