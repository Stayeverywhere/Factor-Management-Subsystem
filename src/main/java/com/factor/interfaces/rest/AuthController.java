package com.factor.interfaces.rest;

import com.factor.application.auth.AuthApplicationService;
import com.factor.common.api.ApiResponse;
import com.factor.interfaces.rest.dto.LoginRequest;
import com.factor.interfaces.rest.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(LoginVO.from(authApplicationService.login(request.username(), request.password(), request.userType())));
    }
}
