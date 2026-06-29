package com.factor.domain.factor;

public record DataSource(
        String id,
        String name,
        String type,
        String configJson,
        boolean enabled
) {
}
