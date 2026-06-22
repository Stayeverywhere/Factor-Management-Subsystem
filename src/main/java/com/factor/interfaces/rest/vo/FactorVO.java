package com.factor.interfaces.rest.vo;

import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record FactorVO(
        String id,
        String code,
        String name,
        FactorCategory category,
        String source,
        String description,
        BigDecimal latestValue,
        String unit,
        Map<String, Object> metadata,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FactorVO from(Factor factor) {
        return new FactorVO(
                factor.id(),
                factor.code(),
                factor.name(),
                factor.category(),
                factor.source(),
                factor.description(),
                factor.latestValue(),
                factor.unit(),
                factor.metadata(),
                factor.createdAt(),
                factor.updatedAt()
        );
    }
}
