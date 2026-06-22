package com.factor.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RoleUpsertRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String userType,
        @NotBlank String scope,
        @NotEmpty List<String> permissions,
        boolean builtIn
) {
}
