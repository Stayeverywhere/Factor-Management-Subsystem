package com.factor.interfaces.rest;

import com.factor.application.auth.RoleApplicationService;
import com.factor.common.api.ApiResponse;
import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.RoleScope;
import com.factor.domain.auth.UserType;
import com.factor.interfaces.rest.dto.RoleUpsertRequest;
import com.factor.interfaces.rest.vo.RoleVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {

    private final RoleApplicationService roleApplicationService;

    public RoleController(RoleApplicationService roleApplicationService) {
        this.roleApplicationService = roleApplicationService;
    }

    @GetMapping
    public ApiResponse<List<RoleVO>> list() {
        return ApiResponse.ok(roleApplicationService.listRoles().stream().map(RoleVO::from).toList());
    }

    @GetMapping("/templates/{userType}")
    public ApiResponse<List<PermissionCode>> template(@PathVariable String userType) {
        return ApiResponse.ok(roleApplicationService.listTemplatePermissions(userType));
    }

    @PostMapping
    public ApiResponse<RoleVO> create(@Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.ok(RoleVO.from(roleApplicationService.createRole(toRole(null, request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleVO> update(@PathVariable String id, @Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.ok(RoleVO.from(roleApplicationService.updateRole(id, toRole(id, request))));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        roleApplicationService.deleteRole(id);
        return ApiResponse.ok("deleted", null);
    }

    private Role toRole(String id, RoleUpsertRequest request) {
        return new Role(
                id,
                request.code(),
                request.name(),
                UserType.valueOf(request.userType()),
                RoleScope.valueOf(request.scope()),
                request.permissions().stream().map(PermissionCode::valueOf).toList(),
                request.builtIn()
        );
    }
}
