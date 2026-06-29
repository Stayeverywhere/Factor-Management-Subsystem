package com.factor.domain.factor.repository;

import com.factor.domain.factor.StyleFactorItem;

import java.util.List;

public interface StyleFactorItemRepository {
    List<StyleFactorItem> findByStyleFactorId(String styleFactorId);
    List<StyleFactorItem> saveAll(List<StyleFactorItem> items);
}
