# 因子管理子系统

这是一个基于 Java + Spring Boot 的因子管理子系统后端基础工程，采用分层架构设计，便于后续持续扩展为完整的因子数据、因子树、衍生因子与风格因子管理平台。

## 项目目标

面向基金/量化研究场景，支持以下能力的逐步演进：

- 因子基础信息管理
- 因子树管理
- 衍生因子公式构建与计算
- 风格因子管理
- 系统级角色与权限管理
- 交易员银子管理
- 金仓数据库接入
- 鸿蒙应用对接
- Dify 接入 DeepSeek 大模型

## 当前已实现内容

### 基础工程

- Spring Boot 启动入口
- Maven 构建配置
- 基础应用配置 `application.yml`

### 分层架构

- `common`：统一返回体、分页模型、全局异常处理
- `domain`：领域模型与仓储接口
- `application`：应用服务接口与实现
- `infrastructure`：基础设施实现，当前提供内存仓储实现
- `interfaces`：REST 接口层、DTO、VO

### 已实现的业务能力

- 因子列表查询
- 因子详情查询
- 因子分类筛选
- 统一响应封装
- 基础异常处理
- 登录与角色菜单分流
- 系统超级管理员、交易员、客户三类角色
- 管理员后台角色管理接口
- 交易员银子账户管理接口
- 衍生因子公式模型雏形
- 因子树模型雏形
- 数据源模型雏形

## 角色与能力边界

### 系统超级管理员

负责系统级治理与后台管理，包括：

- 租户管理
- 机构管理
- 账号创建删除
- 角色模板配置
- 权限菜单配置
- 系统参数配置
- 风控阈值配置
- 日志审计
- 数据备份
- 接口密钥管理
- 角色管理接口

### 交易员

当前仅实现与银子管理相关的能力，后续可扩展到完整交易执行流程：

- 银子账户开立
- 银子账户列表查询
- 银子账户资金冻结
- 银子账户资金解冻
- 银子账户资金划转
- 交易流水查询

### 客户

可查看自己的业务页面与信息：

- 我的产品
- 我的组合
- 我的协议
- 我的持仓
- 我的收益
- 信息披露
- 签署协议
- 申请赎回

## 项目架构

```text
src/main/java/com/factor
├── FactorManagementSubsystemApplication.java
├── common
│   ├── api
│   │   └── ApiResponse.java
│   ├── model
│   │   └── PageResult.java
│   └── exception
│       ├── BusinessException.java
│       └── GlobalExceptionHandler.java
├── domain
│   ├── auth
│   │   ├── Account.java
│   │   ├── AuthSession.java
│   │   ├── MenuItem.java
│   │   ├── PermissionCode.java
│   │   ├── Role.java
│   │   ├── RoleScope.java
│   │   ├── UserType.java
│   │   └── repository
│   │       ├── AccountRepository.java
│   │       └── RoleRepository.java
│   ├── factor
│   │   ├── DataSource.java
│   │   ├── DerivedFactor.java
│   │   ├── Factor.java
│   │   ├── FactorCategory.java
│   │   ├── FactorTree.java
│   │   ├── FactorTreeNode.java
│   │   ├── Formula.java
│   │   ├── FormulaItem.java
│   │   └── StyleFactor.java
│   │   └── repository
│   │       └── FactorRepository.java
│   └── trade
│       ├── TradeCurrencyAccount.java
│       ├── TradeTransaction.java
│       └── repository
│           ├── TradeCurrencyAccountRepository.java
│           └── TradeTransactionRepository.java
├── application
│   ├── auth
│   │   ├── AuthApplicationService.java
│   │   ├── AuthApplicationServiceImpl.java
│   │   ├── RoleApplicationService.java
│   │   ├── RoleApplicationServiceImpl.java
│   │   └── RoleTemplateRegistry.java
│   ├── factor
│   │   ├── DerivedFactorCalculator.java
│   │   ├── FactorApplicationService.java
│   │   └── FactorApplicationServiceImpl.java
│   └── trade
│       ├── TraderCurrencyService.java
│       └── TraderCurrencyServiceImpl.java
├── infrastructure
│   └── persistence
│       ├── InMemoryAccountRepository.java
│       ├── InMemoryFactorRepository.java
│       ├── InMemoryRoleRepository.java
│       ├── InMemoryTradeCurrencyAccountRepository.java
│       └── InMemoryTradeTransactionRepository.java
└── interfaces
    └── rest
        ├── AuthController.java
        ├── FactorController.java
        ├── RoleController.java
        ├── TradeController.java
        ├── dto
        │   ├── CurrencyOpenRequest.java
        │   ├── CurrencyTransferRequest.java
        │   ├── FactorQueryRequest.java
        │   ├── FreezeRequest.java
        │   ├── LoginRequest.java
        │   └── RoleUpsertRequest.java
        └── vo
            ├── FactorVO.java
            ├── LoginVO.java
            └── RoleVO.java
```

## 预留扩展点

为了兼容后续真实业务落地，当前代码已为以下能力预留扩展接口：

- `FactorRepository`：后续可替换为金仓数据库实现
- `RoleRepository` / `AccountRepository`：后续可替换为正式权限与账号中心
- `TradeCurrencyAccountRepository` / `TradeTransactionRepository`：后续可替换为银子账户数据库实现
- `FactorApplicationService`：后续可继续扩展因子写入、更新、删除、批量导入等用例
- `TraderCurrencyService`：后续可扩展为完整交易执行、账户管理、资金划拨、清算能力
- `GlobalExceptionHandler`：后续可统一扩展校验异常、权限异常、外部系统异常
- `interfaces/rest/dto` 与 `interfaces/rest/vo`：后续适合继续增加分页、排序、批量操作参数

## 启动

```bash
mvn spring-boot:run
```

## 示例接口

### 登录

`POST /api/auth/login`

### 角色管理

- `GET /api/admin/roles`
- `GET /api/admin/roles/templates/{userType}`
- `POST /api/admin/roles`
- `PUT /api/admin/roles/{id}`
- `DELETE /api/admin/roles/{id}`

### 交易员银子管理

- `GET /api/trader/currency/accounts?traderId=...`
- `GET /api/trader/currency/transactions?accountId=...`
- `POST /api/trader/currency/open`
- `POST /api/trader/currency/freeze`
- `POST /api/trader/currency/unfreeze`
- `POST /api/trader/currency/transfer`

### 获取因子列表

`GET /api/factors`

可选参数：

- `category`：因子分类，如 `RETURN`、`RISK`、`STYLE`
- `page`：页码，默认 `1`
- `size`：每页条数，默认 `10`

### 获取因子详情

`GET /api/factors/{id}`

## 后续规划建议

1. 为交易员银子管理接口增加更完整的资金校验与权限校验
2. 接入数据库持久化层
3. 补齐因子新增、编辑、删除接口
4. 完善因子树管理接口
5. 完善衍生因子公式引擎与版本管理
6. 接入权限认证与用户体系
7. 对接外部数据源与 AI 能力
