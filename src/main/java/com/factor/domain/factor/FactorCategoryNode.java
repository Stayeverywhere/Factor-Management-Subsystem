package com.factor.domain.factor;

import java.util.List;

public record FactorCategoryNode(
        String id,
        String parentId,
        String name,
        int level,
        int sortNo,
        String description,
        boolean enabled,
        List<FactorCategoryNode> children
) {
}
