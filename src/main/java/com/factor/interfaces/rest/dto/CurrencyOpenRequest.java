package com.factor.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CurrencyOpenRequest(
        @NotBlank String traderId,
        @NotBlank String customerId,
        @NotBlank String customerName,
        @NotBlank String currency
) {
}
