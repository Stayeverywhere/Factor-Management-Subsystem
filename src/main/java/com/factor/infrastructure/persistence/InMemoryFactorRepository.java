package com.factor.infrastructure.persistence;

import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;
import com.factor.domain.factor.repository.FactorRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryFactorRepository implements FactorRepository {

    private final List<Factor> storage = new CopyOnWriteArrayList<>(List.of(
            new Factor("f1", "annual_return", "年化收益率", FactorCategory.RETURN, "Wind", "基金年化收益率", new BigDecimal("0.1234"), "%", null, LocalDateTime.now(), LocalDateTime.now()),
            new Factor("f2", "max_drawdown", "最大回撤", FactorCategory.RISK, "Wind", "基金最大回撤", new BigDecimal("-0.0812"), "%", null, LocalDateTime.now(), LocalDateTime.now())
    ));

    @Override
    public Optional<Factor> findById(String id) {
        return storage.stream().filter(factor -> factor.id().equals(id)).findFirst();
    }

    @Override
    public List<Factor> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public List<Factor> findByCategory(FactorCategory category) {
        return storage.stream().filter(factor -> factor.category() == category).toList();
    }

    @Override
    public Factor save(Factor factor) {
        storage.removeIf(existing -> existing.id().equals(factor.id()));
        storage.add(factor);
        return factor;
    }
}
