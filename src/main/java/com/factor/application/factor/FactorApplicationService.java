package com.factor.application.factor;

import com.factor.common.model.PageResult;
import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;

import java.util.Optional;

public interface FactorApplicationService {
    PageResult<Factor> listFactors(FactorCategory category, long page, long size);

    Optional<Factor> getFactor(String id);
}
