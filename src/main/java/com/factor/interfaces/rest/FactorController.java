package com.factor.interfaces.rest;

import com.factor.application.factor.FactorApplicationService;
import com.factor.common.api.ApiResponse;
import com.factor.common.model.PageResult;
import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;
import com.factor.interfaces.rest.dto.FactorQueryRequest;
import com.factor.interfaces.rest.vo.FactorVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/factors")
public class FactorController {

    private final FactorApplicationService factorApplicationService;

    public FactorController(FactorApplicationService factorApplicationService) {
        this.factorApplicationService = factorApplicationService;
    }

    @GetMapping
    public ApiResponse<PageResult<FactorVO>> list(@ModelAttribute FactorQueryRequest request) {
        PageResult<Factor> result = factorApplicationService.listFactors(request.category(), request.page(), request.size());
        return ApiResponse.ok(new PageResult<>(result.items().stream().map(FactorVO::from).toList(), result.page(), result.size(), result.total()));
    }

    @GetMapping("/{id}")
    public ApiResponse<FactorVO> detail(@PathVariable String id) {
        Factor factor = factorApplicationService.getFactor(id)
                .orElseThrow(() -> new IllegalArgumentException("Factor not found: " + id));
        return ApiResponse.ok(FactorVO.from(factor));
    }
}
