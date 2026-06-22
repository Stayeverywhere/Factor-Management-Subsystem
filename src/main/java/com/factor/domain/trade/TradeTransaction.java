package com.factor.domain.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeTransaction(
        String id,
        String currencyAccountId,
        String transactionType,
        BigDecimal amount,
        String bizNo,
        String description,
        LocalDateTime occurredAt
) {
}
