package com.factor.interfaces.rest;

import com.factor.common.api.ApiResponse;
import com.factor.domain.auth.UserType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping
    public ApiResponse<Map<String, Object>> dashboard(@RequestHeader(value = "X-User-Type", required = false) String userType,
                                                      @RequestParam(required = false) String role) {
        String resolved = role != null ? role : (userType == null ? "SYSTEM_ADMIN" : userType);
        UserType type = UserType.valueOf(resolved);
        return ApiResponse.ok(switch (type) {
            case SYSTEM_ADMIN -> Map.of(
                    "title", "平台治理总览",
                    "kicker", "系统超级管理员后台",
                    "nav", List.of(
                            Map.of("name", "首页"),
                            Map.of("name", "租户管理"),
                            Map.of("name", "机构管理"),
                            Map.of("name", "账号管理"),
                            Map.of("name", "角色管理"),
                            Map.of("name", "权限菜单管理"),
                            Map.of("name", "因子管理"),
                            Map.of("name", "系统参数"),
                            Map.of("name", "风控阈值"),
                            Map.of("name", "审计日志"),
                            Map.of("name", "数据备份"),
                            Map.of("name", "接口密钥")
                    ),
                    "stats", List.of(
                            Map.of("label", "租户数量", "value", "36", "delta", "+3.8%"),
                            Map.of("label", "机构数量", "value", "128", "delta", "+1.2%"),
                            Map.of("label", "账号数量", "value", "1,286", "delta", "+8.4%"),
                            Map.of("label", "角色数量", "value", "18", "delta", "+0.0%")
                    ),
                    "tasks", List.of("待审核角色模板 3 条", "待分配接口密钥 2 条", "风控阈值变更审批 4 条"),
                    "shortcuts", List.of("新建租户", "创建账号", "配置角色模板", "查看审计日志"),
                    "tableTitle", "最近系统操作",
                    "tableHint", "管理员操作留痕",
                    "table", List.of(
                            List.of("张三", "角色管理", "修改", "成功", "2 分钟前"),
                            List.of("李四", "账号管理", "新增", "成功", "15 分钟前"),
                            List.of("系统任务", "数据备份", "执行", "进行中", "1 小时前")
                    )
            );
            case TRADER -> Map.of(
                    "title", "交易执行看板",
                    "kicker", "交易员工作台",
                    "nav", List.of(
                            Map.of("name", "首页"),
                            Map.of("name", "银子账户管理"),
                            Map.of("name", "冻结/解冻管理"),
                            Map.of("name", "资金划转管理"),
                            Map.of("name", "交易流水"),
                            Map.of("name", "交易单管理"),
                            Map.of("name", "交易复核"),
                            Map.of("name", "异常处理")
                    ),
                    "stats", List.of(
                            Map.of("label", "银子账户总数", "value", "248", "delta", "+2.1%"),
                            Map.of("label", "可用资金", "value", "¥ 86.3M", "delta", "+1.6%"),
                            Map.of("label", "冻结金额", "value", "¥ 12.4M", "delta", "-0.8%"),
                            Map.of("label", "待处理交易", "value", "27", "delta", "+9.7%")
                    ),
                    "tasks", List.of("待复核交易单 9 笔", "异常冻结申请 4 笔", "资金划转待确认 6 笔"),
                    "shortcuts", List.of("新开银子账户", "发起冻结", "资金划转", "查看交易流水"),
                    "tableTitle", "最新交易流水",
                    "tableHint", "资金动作实时更新",
                    "table", List.of(
                            List.of("ACC-1001", "冻结", "¥ 500,000", "成功", "1 分钟前"),
                            List.of("ACC-1028", "解冻", "¥ 120,000", "成功", "6 分钟前"),
                            List.of("ACC-1033", "划转", "¥ 80,000", "待确认", "20 分钟前")
                    )
            );
            case CUSTOMER -> Map.of(
                    "title", "我的资产首页",
                    "kicker", "客户门户",
                    "nav", List.of(
                            Map.of("name", "首页"),
                            Map.of("name", "我的产品"),
                            Map.of("name", "我的组合"),
                            Map.of("name", "我的协议"),
                            Map.of("name", "我的持仓"),
                            Map.of("name", "我的收益"),
                            Map.of("name", "信息披露"),
                            Map.of("name", "签署协议"),
                            Map.of("name", "申请赎回")
                    ),
                    "stats", List.of(
                            Map.of("label", "我的总资产", "value", "¥ 1,286,000", "delta", "+2.3%"),
                            Map.of("label", "今日收益", "value", "+¥ 4,820", "delta", "+0.38%"),
                            Map.of("label", "累计收益", "value", "+¥ 126,000", "delta", "+10.8%"),
                            Map.of("label", "当前组合", "value", "3 个", "delta", "稳健型")
                    ),
                    "tasks", List.of("协议待签署 1 份", "披露信息待查看 2 条", "组合赎回申请可办理"),
                    "shortcuts", List.of("查看持仓", "签署协议", "申请赎回", "查看收益"),
                    "tableTitle", "近期组合动态",
                    "tableHint", "面向客户展示",
                    "table", List.of(
                            List.of("稳健成长", "持有中", "收益 +8.2%", "正常", "今日"),
                            List.of("现金增强", "持有中", "收益 +3.5%", "正常", "昨日"),
                            List.of("精选配置", "待签署", "收益 --", "待处理", "2 天前")
                    )
            );
            default -> Map.of("title", "未知角色", "kicker", "请重新登录");
        });
    }
}
