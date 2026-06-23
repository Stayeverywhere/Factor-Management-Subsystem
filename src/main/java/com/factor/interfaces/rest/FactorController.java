package com.factor.interfaces.rest;

import com.factor.application.factor.FactorApplicationService;
import com.factor.common.api.ApiResponse;
import com.factor.common.model.PageResult;
import com.factor.domain.factor.BaseFactor;
import com.factor.domain.factor.BaseFactorValue;
import com.factor.domain.factor.DerivativeFactor;
import com.factor.domain.factor.DerivativeFactorCreateRequest;
import com.factor.domain.factor.DerivativeFactorValue;
import com.factor.domain.factor.FactorCategoryNode;
import com.factor.domain.factor.FactorQueryCondition;
import com.factor.domain.factor.FundInfo;
import com.factor.domain.factor.StyleFactorCreateRequest;
import com.factor.domain.factor.StyleFactorDefinition;
import com.factor.domain.factor.StyleFactorValue;
import com.factor.interfaces.rest.dto.FactorQueryRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/factors")
public class FactorController {

    private final FactorApplicationService factorApplicationService;

    public FactorController(FactorApplicationService factorApplicationService) {
        this.factorApplicationService = factorApplicationService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<FactorCategoryNode>> categories() { return ApiResponse.ok(factorApplicationService.categoryTree()); }

    @GetMapping("/funds")
    public ApiResponse<List<FundInfo>> funds() { return ApiResponse.ok(factorApplicationService.funds()); }

    @GetMapping("/base")
    public ApiResponse<PageResult<BaseFactor>> baseFactors(@RequestParam(required = false) String categoryId,
                                                           @RequestParam(defaultValue = "1") long page,
                                                           @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(factorApplicationService.listBaseFactors(categoryId, page, size));
    }

    @GetMapping("/base/value")
    public ApiResponse<List<BaseFactorValue>> baseFactorValues(@Valid FactorQueryRequest request) {
        return ApiResponse.ok(factorApplicationService.baseFactorValues(new FactorQueryCondition(request.fundCode(), request.factorId(), request.startDate(), request.endDate(), request.page(), request.size())));
    }

    @PostMapping("/base")
    public ApiResponse<BaseFactor> saveBaseFactor(@RequestBody BaseFactor factor) {
        return ApiResponse.ok(factorApplicationService.saveBaseFactor(factor));
    }

    @GetMapping("/derived")
    public ApiResponse<List<DerivativeFactor>> derivedFactors() { return ApiResponse.ok(factorApplicationService.listDerivativeFactors()); }

    @PostMapping("/derived")
    public ApiResponse<DerivativeFactor> createDerived(@Valid @RequestBody DerivativeFactorCreateRequest request) {
        return ApiResponse.ok(factorApplicationService.createDerivativeFactor(request, "system"));
    }

    @GetMapping("/derived/value")
    public ApiResponse<List<DerivativeFactorValue>> derivedFactorValues(@Valid FactorQueryRequest request) {
        return ApiResponse.ok(factorApplicationService.derivativeFactorValues(new FactorQueryCondition(request.fundCode(), request.factorId(), request.startDate(), request.endDate(), request.page(), request.size())));
    }

    @GetMapping("/style")
    public ApiResponse<List<StyleFactorDefinition>> styleFactors() { return ApiResponse.ok(factorApplicationService.listStyleFactors()); }

    @PostMapping("/style")
    public ApiResponse<StyleFactorDefinition> createStyle(@Valid @RequestBody StyleFactorCreateRequest request) {
        return ApiResponse.ok(factorApplicationService.createStyleFactor(request, "system"));
    }

    @GetMapping("/style/value")
    public ApiResponse<List<StyleFactorValue>> styleFactorValues(@Valid FactorQueryRequest request) {
        return ApiResponse.ok(factorApplicationService.styleFactorValues(new FactorQueryCondition(request.fundCode(), request.factorId(), request.startDate(), request.endDate(), request.page(), request.size())));
    }
}
