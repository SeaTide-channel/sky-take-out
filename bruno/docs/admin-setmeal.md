# 套餐相关接口（Setmeal）

对应后端：`SetmealController`，基础路径 **`/admin/setmeal`**。

**认证**：属于 `/admin/**`，需在请求头携带 JWT（与拦截器配置一致，登录接口除外）。在 **`bruno/.env`** 中配置 **`SKY_ADMIN_TOKEN`**（JWT 裸值，勿加引号），请求头 **`token: {{process.env.SKY_ADMIN_TOKEN}}`**（头名称以 `application.yml` 中 `sky.jwt.admin-token-name` 为准，默认为 `token`）。

**Base URL 示例**：`{{baseUrl}}/admin/setmeal`（`{{baseUrl}}` 如 `http://localhost:8080`）。

### 涉及的数据库表

| 表名 | 说明 |
|------|------|
| **`setmeal`** | 套餐主表。 |
| **`setmeal_dish`** | 套餐与菜品的关联表。 |
| **`dish`** | 仅间接：起售套餐时会校验关联菜品是否停售（`SetmealServiceImpl.startOrStop`）。 |

`setmeal.category_id` 与 **`01_sky.sql`** 中 **`category`**（套餐分类 `type=2`）对应；`setmeal_dish.dish_id` 与 **`dish`** 对应。种子数据见 **`mysql/02_seed_setmeal.sql`**。

---

## 1. 新增套餐

| 项 | 说明 |
|----|------|
| **Method** | `POST` |
| **Path** | `/admin/setmeal` |
| **Body** | `application/json`，`SetmealDTO` |
| **说明** | 创建套餐，并维护套餐与菜品关联（`setmealDishes`）。 |

**请求体主要字段（`SetmealDTO`）**

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 新增时通常不传或由后端生成 |
| `categoryId` | Long | 分类 id |
| `name` | String | 套餐名称 |
| `price` | BigDecimal | 套餐价格 |
| `status` | Integer | 0 停用，1 启用 |
| `description` | String | 描述 |
| `image` | String | 图片地址 |
| `setmealDishes` | 数组 | `SetmealDish` 列表，套餐与菜品关系 |

**响应**：统一 `Result`，成功时通常无业务载荷（`Result.success()`）。

---

## 2. 套餐分页查询

| 项 | 说明 |
|----|------|
| **Method** | `GET` |
| **Path** | `/admin/setmeal/page` |
| **Query** | 见下表 |
| **说明** | 分页返回套餐列表（结构为 `PageResult`）。 |

**查询参数（`SetmealPageQueryDTO`）**

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | int | 页码 |
| `pageSize` | int | 每页条数 |
| `name` | String | 套餐名称（可选，模糊等逻辑由服务端实现） |
| `categoryId` | Integer | 分类 id（可选） |
| `status` | Integer | 状态：0 禁用，1 启用（可选） |

**响应**：`Result<PageResult>`。

---

## 3. 批量删除套餐

| 项 | 说明 |
|----|------|
| **Method** | `DELETE` |
| **Path** | `/admin/setmeal` |
| **参数** | `ids`：多个套餐 id（列表） |
| **说明** | 按 id 列表批量删除套餐；服务端会删除关联的 `setmeal_dish`（见 `deleteWithSetmealDish`）。 |

**Query 示例**：`?ids=1&ids=2&ids=3`（重复参数名传递数组，具体以 Spring MVC 绑定为准）。

**响应**：`Result.success()`。

---

## 4. 根据 id 查询套餐

| 项 | 说明 |
|----|------|
| **Method** | `GET` |
| **Path** | `/admin/setmeal/{id}` |
| **Path 变量** | `id`：套餐 id |
| **说明** | 查询单个套餐详情（含关联数据，类型为 `SetmealDTO`）。 |

**响应**：`Result<SetmealDTO>`。

---

## 5. 设置套餐起售/停售

| 项 | 说明 |
|----|------|
| **Method** | `POST` |
| **Path** | `/admin/setmeal/status/{status}` |
| **Path 变量** | `status`：目标状态（与业务约定一致，如 0/1） |
| **参数** | `id`：套餐 id（方法参数为 `Long id`，由 Spring 从 **请求参数** `id` 绑定） |
| **说明** | 更新指定套餐的启停状态。 |

**调用示例**：`POST /admin/setmeal/status/1?id=10`

**响应**：`Result.success()`。

---

## 6. 修改套餐

| 项 | 说明 |
|----|------|
| **Method** | `PUT` |
| **Path** | `/admin/setmeal` |
| **Body** | `application/json`，`SetmealDTO`（需带 `id` 等完整修改字段） |
| **说明** | 更新套餐及关联菜品关系。 |

**响应**：`Result.success()`。

---

## 汇总表

| 功能 | Method | Path |
|------|--------|------|
| 新增套餐 | POST | `/admin/setmeal` |
| 分页查询 | GET | `/admin/setmeal/page` |
| 批量删除 | DELETE | `/admin/setmeal` |
| 按 id 查询 | GET | `/admin/setmeal/{id}` |
| 启停套餐 | POST | `/admin/setmeal/status/{status}` |
| 修改套餐 | PUT | `/admin/setmeal` |

---

## 7. Bruno 环境与集合约定（摘录）

| 变量（`environments/local.bru`） | 用途 |
|----------------------------------|------|
| `baseUrl` | 如 `http://localhost:8080` |
| `token` | 登录后 JWT |
| `setmealId` | 默认 **101**（与种子一致） |
| `setmealIdStopped` | 默认 **103**（种子中为停售） |
| `status`、`deleteSetmealId` | 见集合内说明 |

集合目录：`bruno/admin/setmeal/*.bru`。测试前执行 **`mysql/02_seed_setmeal.sql`** 得到稳定基线。

---

## 8. Bruno 自动化测试执行计划（Test Plan）

基于 **`mysql/02_seed_setmeal.sql`**：种子套餐固定 id **101 / 102 / 103**，脚本末尾将 **`AUTO_INCREMENT` 重置为 1000**，便于新增套餐首条自增 id 为 **1000**（在未手动指定 id、且中间无其它插入的前提下）。建议在 Bruno 中按以下顺序编写断言（可在 `.bru` 的 **Tests** 或 CLI 报告中体现）。

### 阶段一：读操作验证（无副作用，验证种子数据）

- **TC-01：分页查询套餐（正向）**
  - **接口**：`GET /admin/setmeal/page?page=1&pageSize=10`
  - **预期**：成功返回分页数据。
  - **断言建议**：`total` ≥ 3；列表项含 `name` 等字段。

- **TC-02：根据 id 查询套餐（正向）**
  - **接口**：`GET /admin/setmeal/101`（或使用 `{{setmealId}}`）
  - **预期**：返回 id 为 **101** 的套餐及明细。
  - **断言建议**：`data.name` 与种子一致（当前种子为 **`双人饮品套餐`**）；`data.setmealDishes` 长度为 **2**（王老吉、北冰洋）。

### 阶段二：状态与其他行为

- **TC-03：启停套餐（正向）**
  - **接口**：`POST /admin/setmeal/status/1?id=103`（种子 **103** 初始为停售 **0**）
  - **预期**：状态改为起售 **1**。
  - **断言建议**：HTTP 200；业务 `code` 成功；可再 `GET /admin/setmeal/103` 核对 `status`。

- **TC-04：批量删除与「起售」说明（与当前后端对齐）**

  当前 **`SetmealServiceImpl.deleteWithSetmealDish`** **未校验**套餐是否起售，**起售中的套餐也会被删除**。因此：

  - **不要**断言「起售中的 102 删除必然失败」——与本仓库实现不符。
  - 若产品将来要求「起售不可删」，应在后端补校验后再增加对应负例用例。

  可选替代：**删除停售套餐 103**（先在 TC-03 若已改为起售，可先 `POST .../status/0?id=103` 再删），或删除新建测试 id **1000**（见阶段三）。

### 阶段三：增删改闭环（依赖自增从 1000 开始）

- **TC-05：新增套餐（正向）**
  - **接口**：`POST /admin/setmeal`
  - **Body**：新业务名称避免与种子 **`name` 唯一索引**冲突（例如含时间戳或 **`Bruno新建套餐`**）；`categoryId` 可用 **15**；挂载 `01_sky.sql` 中存在的 `dish_id`（如 **46、47**），并填 `setmealDishes`。
  - **预期**：新增成功；数据库下一自增 id 一般为 **1000**（以实际插入为准，可用 `GET page` 或查库确认）。

- **TC-06：修改新套餐（正向）**
  - **接口**：`PUT /admin/setmeal`
  - **Body**：`id` 填 **TC-05** 返回的实际 id（常为 **1000**）；修改名称 / 价格等。
  - **预期**：更新成功；`GET` 详情与修改一致。

- **TC-07：清理测试数据（正向）**
  - **接口**：`DELETE /admin/setmeal?ids=1000`（id 与 TC-05/06 一致）
  - **预期**：当前实现下**无需先停售**即可删除；成功后 **`GET /admin/setmeal/1000`** 应无数据或业务失败。
  - **断言建议**：删除成功；必要时再跑 **`02_seed_setmeal.sql`** 恢复 101–103 基线。

---

### 备注

- **`token`**：开发阶段可在 `local.bru` 使用登录接口返回值；勿将真实 token 提交版本库。
- **种子与 Bruno**：`setmealId`、`deleteSetmealId` 等应与 **`02_seed_setmeal.sql`**、当前库数据保持一致；改名种子后请同步改断言中的套餐名称与 id。
