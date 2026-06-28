package com.factor.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record DerivativeFactorCreateRequest(
        @NotBlank String name,
        @NotEmpty List<Item> items,
        String formula
) {
    public record Item(@NotBlank String baseFactorId, @NotNull BigDecimal weight) {}
}
