package com.factor.application.auth;

import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.UserType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RoleTemplateRegistry {

    private static final Map<UserType, Set<PermissionCode>> DEFAULT_TEMPLATE = new EnumMap<>(UserType.class);

    static {
        DEFAULT_TEMPLATE.put(UserType.SYSTEM_ADMIN, EnumSet.allOf(PermissionCode.class));
        DEFAULT_TEMPLATE.put(UserType.TRADER, EnumSet.of(
                PermissionCode.TRADE_ORDER_VIEW,
                PermissionCode.TRADE_ORDER_EXECUTE,
                PermissionCode.TRADE_ORDER_REVIEW,
                PermissionCode.PORTFOLIO_VIEW,
                PermissionCode.DISCLOSURE_VIEW
        ));
        DEFAULT_TEMPLATE.put(UserType.CUSTOMER, EnumSet.of(
                PermissionCode.PORTFOLIO_VIEW,
                PermissionCode.AGREEMENT_VIEW,
                PermissionCode.AGREEMENT_SIGN,
                PermissionCode.REDEMPTION_REQUEST,
                PermissionCode.DISCLOSURE_VIEW
        ));
    }

    private RoleTemplateRegistry() {
    }

    public static Set<PermissionCode> defaultPermissions(UserType userType) {
        return DEFAULT_TEMPLATE.getOrDefault(userType, EnumSet.of(PermissionCode.EXTENDABLE));
    }
}
