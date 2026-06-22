package com.factor.domain.factor;

import java.time.LocalDateTime;
import java.util.List;

public record Formula(
        String id,
        String name,
        String expression,
        List<FormulaItem> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
