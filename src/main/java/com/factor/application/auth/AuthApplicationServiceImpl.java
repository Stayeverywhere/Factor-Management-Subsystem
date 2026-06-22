package com.factor.application.auth;

import com.factor.common.exception.BusinessException;
import com.factor.domain.auth.Account;
import com.factor.domain.auth.AuthSession;
import com.factor.domain.auth.MenuItem;
import com.factor.domain.auth.PermissionCode;
import com.factor.domain.auth.Role;
import com.factor.domain.auth.UserType;
import com.factor.domain.auth.repository.AccountRepository;
import com.factor.domain.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    public AuthApplicationServiceImpl(AccountRepository accountRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public AuthSession login(String username, String password, String userType) {
        UserType requestedType = UserType.valueOf(userType);
        Account account = accountRepository.findByUsername(username)
                .filter(a -> a.userType() == requestedType)
                .filter(Account::enabled)
                .filter(a -> a.passwordHash().equals(password))
                .orElseThrow(() -> new BusinessException("用户名、密码或身份不正确"));

        Role role = roleRepository.findById(account.roleId())
                .orElseThrow(() -> new BusinessException("角色不存在"));

        return new AuthSession(
                "token-" + account.username() + "-" + account.userType(),
                account,
                role,
                buildMenus(account.userType(), role.permissions())
        );
    }

    private List<MenuItem> buildMenus(UserType userType, List<PermissionCode> permissions) {
        if (userType == UserType.SYSTEM_ADMIN) {
            return List.of(
                    new MenuItem("m1", null, "租户管理", "/tenants", "TenantPage", "tenant", PermissionCode.TENANT_MANAGE, List.of()),
                    new MenuItem("m2", null, "机构管理", "/organizations", "OrganizationPage", "organization", PermissionCode.ORGANIZATION_MANAGE, List.of()),
                    new MenuItem("m3", null, "账号管理", "/accounts", "AccountPage", "account", PermissionCode.ACCOUNT_MANAGE, List.of()),
                    new MenuItem("m4", null, "角色模板配置", "/roles", "RolePage", "role", PermissionCode.ROLE_TEMPLATE_CONFIG, List.of()),
                    new MenuItem("m5", null, "权限菜单配置", "/permissions", "PermissionPage", "permission", PermissionCode.PERMISSION_MENU_CONFIG, List.of()),
                    new MenuItem("m6", null, "系统参数配置", "/system-settings", "SystemSettingPage", "setting", PermissionCode.SYSTEM_PARAMETER_CONFIG, List.of()),
                    new MenuItem("m7", null, "风控阈值", "/risk-threshold", "RiskThresholdPage", "risk", PermissionCode.RISK_THRESHOLD_CONFIG, List.of()),
                    new MenuItem("m8", null, "日志审计", "/audit-logs", "AuditLogPage", "audit", PermissionCode.LOG_AUDIT, List.of()),
                    new MenuItem("m9", null, "数据备份", "/backups", "BackupPage", "backup", PermissionCode.DATA_BACKUP, List.of()),
                    new MenuItem("m10", null, "接口密钥", "/api-keys", "ApiKeyPage", "key", PermissionCode.API_KEY_MANAGE, List.of())
            );
        }
        return List.of(
                new MenuItem("c1", null, "因子管理", "/factors", "FactorPage", "factor", PermissionCode.FACTOR_READ, List.of()),
                new MenuItem("c2", null, "因子树", "/factor-tree", "FactorTreePage", "tree", PermissionCode.FACTOR_TREE_MANAGE, List.of()),
                new MenuItem("c3", null, "衍生因子", "/derived-factors", "DerivedFactorPage", "formula", PermissionCode.DERIVED_FACTOR_MANAGE, List.of()),
                new MenuItem("c4", null, "风格因子", "/style-factors", "StyleFactorPage", "style", PermissionCode.STYLE_FACTOR_MANAGE, List.of())
        );
    }
}
