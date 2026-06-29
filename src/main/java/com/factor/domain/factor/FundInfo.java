package com.factor.domain.factor;

import java.time.LocalDate;

public record FundInfo(
        String fundCode,
        String fundName,
        String fundShortName,
        String fundType,
        LocalDate establishmentDate,
        String issuer,
        String fundManager,
        String status
) {
}
