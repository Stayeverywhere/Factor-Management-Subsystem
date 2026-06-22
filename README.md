# 因子管理子系统

这是一个基于 Java + Spring Boot 的因子管理子系统后端基础工程，采用分层架构设计，便于后续持续扩展为完整的因子数据、因子树、衍生因子与风格因子管理平台。

## 项目目标

面向基金/量化研究场景，支持以下能力的逐步演进：

- 因子基础信息管理
- 因子树管理
- 衍生因子公式构建与计算
- 风格因子管理
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
- 衍生因子公式模型雏形
- 因子树模型雏形
- 数据源模型雏形

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
│   └── factor
│       ├── DataSource.java
│       ├── DerivedFactor.java
│       ├── Factor.java
│       ├── FactorCategory.java
│       ├── FactorTree.java
│       ├── FactorTreeNode.java
│       ├── Formula.java
│       ├── FormulaItem.java
│       ├── StyleFactor.java
│       └── repository
│           └── FactorRepository.java
├── application
│   └── factor
│       ├── DerivedFactorCalculator.java
│       ├── FactorApplicationService.java
│       └── FactorApplicationServiceImpl.java
├── infrastructure
│   └── persistence
│       └── InMemoryFactorRepository.java
└── interfaces
    └── rest
        ├── FactorController.java
        ├── dto
        │   └── FactorQueryRequest.java
        └── vo
            └── FactorVO.java
```

## 预留扩展点

为了兼容后续真实业务落地，当前代码已为以下能力预留扩展接口：

- `FactorRepository`：后续可替换为金仓数据库实现
- `FactorApplicationService`：后续可继续扩展因子写入、更新、删除、批量导入等用例
- `GlobalExceptionHandler`：后续可统一扩展校验异常、权限异常、外部系统异常
- `interfaces/rest/dto` 与 `interfaces/rest/vo`：后续适合继续增加分页、排序、批量操作参数

## 启动

```bash
mvn spring-boot:run
```

## 示例接口

### 获取因子列表

`GET /api/factors`

可选参数：

- `category`：因子分类，如 `RETURN`、`RISK`、`STYLE`
- `page`：页码，默认 `1`
- `size`：每页条数，默认 `10`

### 获取因子详情

`GET /api/factors/{id}`

## 后续规划建议

1. 接入数据库持久化层
2. 补齐因子新增、编辑、删除接口
3. 完善因子树管理接口
4. 完善衍生因子公式引擎与版本管理
5. 接入权限认证与用户体系
6. 对接外部数据源与 AI 能力
