package com.factor.application.factor;

import com.factor.common.model.PageResult;
import com.factor.domain.factor.BaseFactor;
import com.factor.domain.factor.BaseFactorValue;
import com.factor.domain.factor.DerivativeFactor;
import com.factor.domain.factor.DerivativeFactorCreateRequest;
import com.factor.domain.factor.DerivativeFactorValue;
import com.factor.domain.factor.FactorCategoryNode;
import com.factor.domain.factor.FactorQueryCondition;
import com.factor.domain.factor.FundInfo;
import com.factor.domain.factor.StyleFactorDefinition;
import com.factor.domain.factor.StyleFactorCreateRequest;
import com.factor.domain.factor.StyleFactorValue;

import java.util.List;
import java.util.Optional;

public interface FactorApplicationService {
    List<FactorCategoryNode> categoryTree();
    List<FundInfo> funds();
    PageResult<BaseFactor> listBaseFactors(String categoryId, long page, long size);
    Optional<BaseFactor> getBaseFactor(String id);
    BaseFactor saveBaseFactor(BaseFactor factor);
    List<BaseFactorValue> baseFactorValues(FactorQueryCondition condition);
    DerivativeFactor createDerivativeFactor(DerivativeFactorCreateRequest request, String createdBy);
    DerivativeFactor updateDerivativeFactor(String id, DerivativeFactorCreateRequest request, String updatedBy);
    void deleteDerivativeFactor(String id);
    List<DerivativeFactor> listDerivativeFactors();
    List<DerivativeFactorValue> derivativeFactorValues(FactorQueryCondition condition);
    StyleFactorDefinition createStyleFactor(StyleFactorCreateRequest request, String createdBy);
    StyleFactorDefinition updateStyleFactor(String id, StyleFactorCreateRequest request, String updatedBy);
    void deleteStyleFactor(String id);
    List<StyleFactorDefinition> listStyleFactors();
    List<StyleFactorValue> styleFactorValues(FactorQueryCondition condition);
}
