package com.factor.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record FreezeRequest(
        @NotBlank String accountId,
        @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String bizNo,
        @NotBlank String description
) {
}
