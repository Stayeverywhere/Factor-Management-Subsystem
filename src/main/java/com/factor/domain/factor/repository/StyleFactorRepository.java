package com.factor.domain.factor.repository;

import com.factor.domain.factor.StyleFactorDefinition;

import java.util.List;
import java.util.Optional;

public interface StyleFactorRepository {
    List<StyleFactorDefinition> findAll();
    Optional<StyleFactorDefinition> findById(String id);
    StyleFactorDefinition save(StyleFactorDefinition factor);
}
