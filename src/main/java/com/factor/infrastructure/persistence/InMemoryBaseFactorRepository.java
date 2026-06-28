package com.factor.infrastructure.persistence;

import com.factor.domain.factor.BaseFactor;
import com.factor.domain.factor.repository.BaseFactorRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("!jpa")
@Repository
public class InMemoryBaseFactorRepository implements BaseFactorRepository {

    private final List<BaseFactor> storage = new ArrayList<>(List.of(
            new BaseFactor("bf-1", "management_fee", "管理费率", "cat-1-1", "数值型", "%", "月度", "Wind", "基金公告/费率表", true, true, "管理费率"),
            new BaseFactor("bf-2", "nav", "单位净值", "cat-3-1", "数值型", "元", "日频", "Wind", "每日净值", true, true, "单位净值")
    ));

    @Override
    public List<BaseFactor> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<BaseFactor> findById(String id) {
        return storage.stream().filter(item -> item.id().equals(id)).findFirst();
    }

    @Override
    public BaseFactor save(BaseFactor factor) {
        BaseFactor stored = factor.id() == null ? new BaseFactor(UUID.randomUUID().toString(), factor.code(), factor.name(), factor.categoryId(), factor.dataType(), factor.unit(), factor.updateFrequency(), factor.dataSource(), factor.fetchLogic(), factor.enabled(), factor.derivable(), factor.description()) : factor;
        storage.removeIf(item -> item.id().equals(stored.id()));
        storage.add(stored);
        return stored;
    }
}
