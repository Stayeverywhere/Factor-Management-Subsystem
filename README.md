# 因子管理子系统

这是一个基于 Java + Spring Boot 的因子管理子系统后端基础工程，并配套 Vue 前端界面，采用分层架构设计，便于后续持续扩展为完整的因子数据、因子树、衍生因子、风格因子与投顾管理平台。

## 项目目标

面向基金/量化研究与投顾管理场景，支持以下能力的逐步演进：

- 因子基础信息管理
- 因子树管理
- 衍生因子公式构建与计算
- 风格因子管理
- 交易员银子管理
- 系统角色与权限管理
- 金仓数据库接入
- 鸿蒙应用对接
- Dify 接入 DeepSeek 大模型

## 推荐定位

- 专业
- 克制
- 金融感
- 数据化
- 易操作

核心感受：

- 看起来稳重可信
- 页面信息密度高，但不拥挤
- 管理端专业
- 客户端简洁易懂
- 角色切换后页面风格统一，但功能差异明显

## 当前已实现内容

### 基础工程

- Spring Boot 启动入口
- Maven 构建配置
- 基础应用配置 `application.yml`

### 后端分层架构

- `common`：统一返回体、分页模型、全局异常处理
- `domain`：领域模型与仓储接口
- `application`：应用服务接口与实现
- `infrastructure`：基础设施实现，当前提供内存仓储实现
- `interfaces`：REST 接口层、DTO、VO

### 已实现的业务能力

- 因子分类目录树
- 基金标的管理
- 基础因子管理
- 衍生因子管理
- 风格投资因子管理
- 因子数值查询
- 因子分类筛选
- 统一响应封装
- 基础异常处理
- 登录与角色菜单返回
- 系统超级管理员、交易员、客户的角色基础
- 管理员角色管理接口
- 交易员银子账户管理接口
- 后端仪表盘数据接口
- 衍生因子公式模型雏形
- 因子树模型雏形
- 数据源模型雏形

## 前端界面原型

前端已由静态页面重构为 Vue + Vite 单页应用，前后端通过接口联调，并采用 Vue Router 拆分为登录页与工作台页面。

### 前端工程目录

- `frontend-vue/`

### 前端当前能力

- 登录页真正调用后端 `POST /api/auth/login`
- 菜单数据从后端登录结果中获取并渲染
- 页面表格数据从后端 `GET /api/dashboard` 拉取
- 因子主页面支持左侧树形分类、顶部筛选区、折线图、数据表
- 创建衍生因子弹窗支持两步式穿梭框 + 权重配置
- 创建风格投资因子弹窗支持两步式穿梭框 + 权重配置
- 使用 Vue 3 + Element Plus + ECharts 构建动态页面
- 使用 Vue Router 拆分 `LoginPage` 与 `WorkspacePage`

### 前端路由设计

- `/login`：登录页
- `/workspace/:role`：按角色进入工作台

### 前端角色视图

- 系统超级管理员端：租户、机构、账号、角色、权限、审计、参数配置
- 交易员端：银子账户、冻结/解冻、资金划转、交易流水、交易复核
- 客户端：我的产品、组合、协议、持仓、收益、披露、赎回
- 因子研究视图：全部因子、因子树、衍生因子、风格因子、数据源、策略管理

### 因子页面交互

- 左侧树形分类菜单：展示费率水平、规模与仓位等层级目录
- 顶部筛选区：基金选择、因子选择、日期范围
- 中部折线图：展示因子时间序列
- 底部数据表格：展示明细数值
- 创建衍生因子：两步式穿梭框 + 权重校验
- 创建风格投资因子：两步式穿梭框 + 权重校验

## 项目目录结构

```text
Factor Management Subsystem
├── pom.xml
├── README.md
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── factor
│       │           ├── FactorManagementSubsystemApplication.java
│       │           ├── common
│       │           ├── domain
│       │           ├── application
│       │           ├── infrastructure
│       │           └── interfaces
│       └── resources
│           └── application.yml
├── frontend-vue
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src
│       ├── main.js
│       ├── App.vue
│       ├── router.js
│       ├── api.js
│       ├── styles.css
│       ├── pages
│       │   ├── LoginPage.vue
│       │   └── WorkspacePage.vue
│       └── components
│           ├── FactorPage.vue
│           └── FactorDialog.vue
└── .gitignore
```

## 预留扩展点

为了兼容后续真实业务落地，当前代码已为以下能力预留扩展接口：

- `FactorRepository`：后续可替换为金仓数据库实现
- `RoleRepository`、`AccountRepository`：后续可替换为真实账号/权限库
- `TradeCurrencyAccountRepository`：后续可替换为交易与资金持久化实现
- `FactorApplicationService`、`RoleApplicationService`、`TraderCurrencyService`：后续可继续扩展新增、编辑、审批、撤销、批量导入等用例
- `GlobalExceptionHandler`：后续可统一扩展校验异常、权限异常、外部系统异常
- `interfaces/rest/dto` 与 `interfaces/rest/vo`：后续适合继续增加分页、排序、批量操作参数

## 启动方式

### 1. 启动后端

请进入项目根目录运行后端命令，也就是包含 `pom.xml` 的目录：

```bash
cd "d:/Factor Management Subsystem"
mvn spring-boot:run
```

默认后端地址：`http://localhost:8081`

### 2. 启动前端 Vue 工程

请进入前端目录：

```bash
cd "d:/Factor Management Subsystem/frontend-vue"
npm install
npm run dev
```

默认前端地址：`http://localhost:5173`

Vite 已配置代理，前端请求 `/api` 会转发到后端 `http://localhost:8081`。

## 前端说明

### 登录方式

前端登录页提供 3 个模拟账号，可分别进入不同角色工作台：

- `admin / admin123`：系统超级管理员
- `trader / trader123`：业务经理 / 交易员
- `customer / customer123`：客户

### 页面说明

- 登录页：后台统一登录入口，支持角色选择
- 工作台：左侧菜单 + 顶栏 + 数据卡片 + 表格
- 因子主页面：左侧树形分类 + 顶部筛选 + 折线图 + 表格
- 衍生因子创建：两步式穿梭框 + 权重校验
- 风格因子创建：两步式穿梭框 + 权重校验

### 配色建议

当前原型采用金融蓝灰风：

- 主色：`#1F4E79`、`#2F80ED`
- 辅助色：`#1CC8A0`、`#F5B800`
- 页面背景：`#F5F7FA`
- 卡片背景：`#FFFFFF`
- 分割线：`#E5EAF2`

## 示例接口

### 登录

`POST /api/auth/login`

### 仪表盘

`GET /api/dashboard?role=SYSTEM_ADMIN`

`GET /api/dashboard?role=TRADER`

`GET /api/dashboard?role=CUSTOMER`

### 因子管理

- `GET /api/factors/categories`
- `GET /api/factors/funds`
- `GET /api/factors/base`
- `GET /api/factors/base/value`
- `POST /api/factors/base`
- `GET /api/factors/derived`
- `POST /api/factors/derived`
- `GET /api/factors/derived/value`
- `GET /api/factors/style`
- `POST /api/factors/style`
- `GET /api/factors/style/value`

### 管理员角色管理

- `GET /api/admin/roles`
- `GET /api/admin/roles/templates/{userType}`
- `POST /api/admin/roles`
- `PUT /api/admin/roles/{id}`
- `DELETE /api/admin/roles/{id}`

### 交易员银子管理

- `GET /api/trader/currency/accounts`
- `GET /api/trader/currency/transactions`
- `POST /api/trader/currency/open`
- `POST /api/trader/currency/freeze`
- `POST /api/trader/currency/unfreeze`
- `POST /api/trader/currency/transfer`

## 后续规划建议

1. 接入真实数据库持久化层
2. 完善账号管理后台（仅超级管理员）
3. 完善交易员银子管理和交易流水
4. 完善客户门户页面与信息披露
5. 完善因子树、衍生因子与风格因子接口
6. 接入权限认证与菜单路由控制
7. 对接外部数据源与 AI 能力
