# 因子管理子系统 (Factor Management Subsystem)

基于 Java Spring Boot + Vue 3 + KingbaseES 的量化因子研究与投顾管理平台，支持因子管理、衍生因子计算、多因子分析、智能 AI 助手等功能。

---

## 技术栈

| 层级 | 技术 | 版本 |
|---|---|---|
| 后端框架 | Spring Boot | 3.3.2 |
| 数据库 | KingbaseES (PostgreSQL 兼容) | V9 |
| ORM | Spring Data JPA + Hibernate | 6.5.2 |
| 前端框架 | Vue 3 + Vite | 5.4 |
| UI 组件 | Element Plus | 2.8 |
| 图表 | ECharts | 5.5 |
| 测试 | Vitest + @vue/test-utils | 1.6 |
| 数据同步 | Python + akshare + SQLAlchemy | - |
| AI 助手 | Dify + DeepSeek | - |

---

## 项目结构

```
Factor Management Subsystem/
├── pom.xml                          # Maven 构建
├── README.md
├── .gitignore
│
├── src/main/java/com/factor/        # Java 后端
│   ├── FactorManagementSubsystemApplication.java
│   ├── common/                      # 统一响应、分页、异常处理
│   ├── domain/                      # 领域模型 & 仓储接口
│   │   ├── factor/                  #   - 因子、分类、衍生因子、风格因子
│   │   ├── auth/                    #   - 账号、角色、权限
│   │   └── trade/                   #   - 交易账户、交易流水
│   ├── application/                 # 应用服务
│   ├── infrastructure/persistence/  # 数据持久化
│   │   ├── InMemory*Repository      # 内存仓储（dev 模式）
│   │   └── jpa/                     # JPA 仓储（jpa 模式）
│   └── interfaces/rest/            # REST 控制器
│       ├── AuthController.java
│       ├── FactorController.java
│       ├── FactorAnalysisController.java  # 多因子分析 API
│       ├── AiChatController.java          # Dify AI 代理
│       └── ...
│
├── src/main/resources/
│   ├── application.yml              # 应用配置
│   └── schema.sql                   # 数据库建表 & 初始数据
│
├── src/test/java/                    # 后端测试
│
├── frontend-vue/                    # Vue 前端
│   ├── package.json
│   ├── vitest.config.js             # Vitest 配置
│   ├── vite.config.js
│   └── src/
│       ├── api.js                   # HTTP 接口封装
│       ├── router.js                # 路由配置
│       ├── styles.css               # 全局样式
│       ├── pages/
│       │   ├── LoginPage.vue        # 登录页
│       │   ├── WorkspacePage.vue    # 工作台 + 导航栏
│       │   ├── FactorOverviewPage.vue  # 因子查询主页
│       │   ├── DerivativeFactorPage.vue # 衍生因子管理
│       │   ├── StyleFactorPage.vue  # 风格因子管理
│       │   ├── MultiFactorPage.vue  # 多因子分析
│       │   └── ...
│       ├── components/
│       │   ├── FactorDialog.vue     # 因子弹窗组件
│       │   └── ChatSidebar.vue      # AI 聊天侧栏
│       └── test/                    # 前端测试
│           ├── vitest.config.js
│           ├── setup.js
│           ├── utils/               # 工具函数测试（12 个）
│           ├── api/                 # API 测试（18 个）
│           ├── pages/               # 页面测试（7 个）
│           ├── components/          # 组件测试（3 个）
│           └── integration/         # 集成测试（2 个）
│
├── akshare-factor-sync/             # Python 数据同步
│   ├── config/settings.py           # 数据库配置
│   ├── init_db.py                   # 数据库初始化
│   ├── run_once.py                  # 数据同步入口
│   └── sync/
│       ├── fund_basic.py            # 基金基本信息同步
│       ├── fund_net_value.py        # 基金净值同步
│       ├── derived_factors.py       # 量价衍生因子计算
│       ├── orchestrator.py          # 同步调度器
│       └── ...
│
└── .gitignore
```

---

## 功能清单

### 数据层
| 功能 | 说明 |
|---|---|
| 基金基本信息 | 27,128 只基金（代码、名称、类型） |
| 基础因子 | 11 个（管理费率、单位净值、规模、仓位等） |
| 因子值 | 200 只基金 × 11 因子 × 91 天 ≈ 20 万条 |
| 衍生因子 | 26 个量价因子（日收益率、MA、动量、RSI、波动率等） |
| 衍生因子值 | 200 只基金 × 26 衍生因子 × 91 天 ≈ 47 万条 |
| 风格因子 | 1 个（稳健风格因子） |
| 风格因子值 | 200 只基金 × 91 天 |

### 页面功能
| 页面 | 路由 | 功能 |
|---|---|---|
| 登录 | `/login` | 角色选择 + 账号密码登录 |
| 因子查询 | `/workspace/customer/factor-overview` | 因子树 + 筛选 + 折线图 + 数据表格 |
| 衍生因子管理 | `/workspace/customer/derived-factor` | 列表/创建/编辑/删除 + 公式编辑器 |
| 风格因子管理 | `/workspace/customer/style-factor` | 列表/创建/编辑/删除 |
| 多因子分析 | `/workspace/customer/multi-factor` | KPI 卡片 + IC 时序 + 热力图 + 建模池(≤7) |
| AI 助手 | 导航栏 🤖 按钮 | 侧边栏聊天（Dify 驱动） |

### 衍生因子公式编辑器
支持从数据库字段选择构建公式：
```
可用字段: close_price, open_price, high_price, low_price, volume, turnover, value
函数: shift(field,n), mean(field,n), max(field,n), min(field,n), std(field,n), abs(x)
运算符: +, -, *, /, (, )
示例: (close_price / shift(close_price, 1) - 1) * 100
```

---

## 启动方式

### 1. 数据库初始化（首次）

```bash
cd akshare-factor-sync
python init_db.py           # 创建基础表结构
python run_once.py           # 同步基金数据
python sync/derived_factors.py  # 计算衍生因子
```

### 2. 启动后端

```bash
cd "d:/Factor Management Subsystem"
mvn spring-boot:run
# 默认 http://localhost:8081
```

使用 `jpa` profile 连接数据库（默认），如需切换回内存模式：
```yaml
# application.yml
spring.profiles.active: memory
```

### 3. 启动前端

```bash
cd "d:/Factor Management Subsystem/frontend-vue"
npm install
npm run dev
# 默认 http://localhost:5173
```

Vite 已配置代理，`/api` 请求自动转发到后端 `http://localhost:8081`。

---

## 测试

### 前端测试

```bash
cd frontend-vue
npm run test              # 运行全部 52 个测试
npm run test:coverage     # 生成覆盖率报告
npm run test:watch        # 监听模式
```

测试报告：`test/coverage/lcov-report/index.html`

### 后端测试

```bash
mvn test
mvn jacoco:report          # JaCoCo 覆盖率报告
# target/site/jacoco/index.html
```

---

## 登录账号

| 账号 | 密码 | 角色 |
|---|---|---|
| `admin` | `admin123` | 系统超级管理员 |
| `trader` | `trader123` | 业务经理 |
| `customer` | `customer123` | 客户 |

---

## AI 智能助手

智能助手由 Dify 工作流驱动，配置在 `application.yml`：

```yaml
dify:
  api-url: http://localhost/v1
  api-key: app-你的API-KEY
```

前端通过后端代理调用（API-Key 存储在后端，不暴露给前端）。点击顶部导航栏 🤖 按钮打开聊天侧栏。

---

## 主要 API

### 因子管理
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/factors/categories` | 因子分类树 |
| GET | `/api/factors/funds` | 基金列表 |
| GET | `/api/factors/base` | 基础因子分页 |
| GET | `/api/factors/base/value` | 基础因子值（支持日期过滤） |
| POST | `/api/factors/derived` | 创建衍生因子 |
| PUT | `/api/factors/derived/{id}` | 更新衍生因子 |
| DELETE | `/api/factors/derived/{id}` | 删除衍生因子 |
| GET | `/api/factors/derived/value` | 衍生因子值 |
| POST | `/api/factors/style` | 创建风格因子 |
| GET | `/api/factors/style/value` | 风格因子值 |
| GET | `/api/factors/analysis/performance` | 因子效能榜单 |
| GET | `/api/factors/analysis/correlation` | 因子相关系数矩阵 |

### AI 助手
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/ai/chat` | 发送聊天消息 |
| GET | `/api/ai/conversations` | 会话列表 |
| DELETE | `/api/ai/conversations/{id}` | 删除会话 |

---

## 环境要求

- JDK 21+
- Maven 3.9+
- Node 18+
- KingbaseES V9（或 PostgreSQL 15+）
- Python 3.10+（数据同步用）
- Dify（AI 助手用，可选）

