package com.factor.domain.factor;

import java.time.LocalDateTime;
import java.util.List;

public record FactorTree(
        String id,
        String name,
        String businessScenario,
        List<FactorTreeNode> nodes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
