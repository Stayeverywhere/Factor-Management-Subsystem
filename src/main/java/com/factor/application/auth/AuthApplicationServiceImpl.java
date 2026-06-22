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
        if (userType == UserType.TRADER) {
            return List.of(
                    new MenuItem("t1", null, "交易单管理", "/trade-orders", "TradeOrderPage", "trade", PermissionCode.TRADE_ORDER_VIEW, List.of()),
                    new MenuItem("t2", null, "组合交易执行", "/trade-execute", "TradeExecutePage", "execute", PermissionCode.TRADE_ORDER_EXECUTE, List.of()),
                    new MenuItem("t3", null, "交易复核", "/trade-review", "TradeReviewPage", "review", PermissionCode.TRADE_ORDER_REVIEW, List.of()),
                    new MenuItem("t4", null, "组合持仓", "/portfolios", "PortfolioPage", "portfolio", PermissionCode.PORTFOLIO_VIEW, List.of())
            );
        }
        return List.of(
                new MenuItem("c1", null, "我的产品", "/my-products", "MyProductsPage", "product", PermissionCode.PORTFOLIO_VIEW, List.of()),
                new MenuItem("c2", null, "我的组合", "/my-portfolios", "MyPortfoliosPage", "portfolio", PermissionCode.PORTFOLIO_VIEW, List.of()),
                new MenuItem("c3", null, "我的协议", "/my-agreements", "MyAgreementsPage", "agreement", PermissionCode.AGREEMENT_VIEW, List.of()),
                new MenuItem("c4", null, "我的持仓", "/my-holdings", "MyHoldingsPage", "holding", PermissionCode.PORTFOLIO_VIEW, List.of()),
                new MenuItem("c5", null, "我的收益", "/my-returns", "MyReturnsPage", "return", PermissionCode.PORTFOLIO_VIEW, List.of()),
                new MenuItem("c6", null, "信息披露", "/disclosure", "DisclosurePage", "disclosure", PermissionCode.DISCLOSURE_VIEW, List.of()),
                new MenuItem("c7", null, "签署协议", "/agreement-sign", "AgreementSignPage", "sign", PermissionCode.AGREEMENT_SIGN, List.of()),
                new MenuItem("c8", null, "申请赎回", "/redemption", "RedemptionPage", "redeem", PermissionCode.REDEMPTION_REQUEST, List.of())
        );
    }
}
