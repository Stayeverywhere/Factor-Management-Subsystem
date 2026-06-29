package com.factor.domain.factor;

import java.util.List;

public record FactorTreeNode(
        String id,
        String factorId,
        String parentId,
        String displayName,
        int sortOrder,
        List<FactorTreeNode> children
) {
}
