package com.factor.domain.factor;

public record BaseFactor(
        String id,
        String code,
        String name,
        String categoryId,
        String dataType,
        String unit,
        String updateFrequency,
        String dataSource,
        String fetchLogic,
        boolean enabled,
        boolean derivable,
        String description
) {
}
