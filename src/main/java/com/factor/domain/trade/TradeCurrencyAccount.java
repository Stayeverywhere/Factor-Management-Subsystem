package com.factor.domain.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeCurrencyAccount(
        String id,
        String traderId,
        String customerId,
        String customerName,
        String currency,
        BigDecimal availableAmount,
        BigDecimal frozenAmount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
