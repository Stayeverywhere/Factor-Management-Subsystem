package com.factor.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CurrencyTransferRequest(
        @NotBlank String fromAccountId,
        @NotBlank String toAccountId,
        @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String bizNo,
        @NotBlank String description
) {
}
