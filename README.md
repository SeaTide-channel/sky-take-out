# Sky Take-Out 外卖管理系统

## 项目介绍

Sky Take-Out 是一个基于 Spring Boot 的外卖管理系统后端服务，提供完整的商家管理、菜品管理、订单管理等功能。

## 技术栈

- **框架**: Spring Boot 3.2.x
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis
- **文件存储**: 阿里云 OSS
- **API文档**: Swagger/OpenAPI
- **日志框架**: SLF4J + Logback

## 项目结构

```
sky-take-out/
├── sky-common/           # 通用模块
│   ├── src/main/java/com/sky/
│   │   ├── constant/     # 常量定义
│   │   ├── context/      # 上下文工具
│   │   ├── enumeration/  # 枚举类
│   │   ├── exception/    # 异常类
│   │   ├── json/         # JSON 工具
│   │   ├── properties/   # 配置属性类
│   │   ├── result/       # 返回结果封装
│   │   └── utils/        # 工具类
├── sky-pojo/             # 数据对象模块
│   ├── src/main/java/com/sky/
│   │   ├── dto/          # 数据传输对象
│   │   ├── entity/       # 实体类
│   │   └── vo/           # 视图对象
└── sky-server/           # 服务模块
    ├── src/main/java/com/sky/
    │   ├── annotation/   # 自定义注解
    │   ├── aspect/       # AOP 切面
    │   ├── config/       # 配置类
    │   ├── controller/   # 控制器
    │   ├── handler/      # 异常处理器
    │   ├── interceptor/  # 拦截器
    │   ├── mapper/       # MyBatis Mapper
    │   ├── service/      # 服务层
    │   └── SkyApplication.java  # 启动类
    └── src/main/resources/
        ├── mapper/       # MyBatis XML 配置
        ├── application.yml      # 主配置文件
        └── application-dev.yml  # 开发环境配置
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE sky_take_out CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件 `sky-server/src/main/resources/application-dev.yml`：
```yaml
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_take_out
    username: your_username
    password: your_password
```

### 阿里云 OSS 配置

修改配置文件中的 OSS 信息：
```yaml
sky:
  alioss:
    endpoint: your_endpoint
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    bucket-name: your_bucket_name
```

### 启动项目

```bash
cd sky-take-out
mvn clean compile
cd sky-server
mvn spring-boot:run
```

服务启动后访问：http://localhost:8080

如果在 `sky-server` 目录单独执行 `mvn spring-boot:run` 时出现 `Could not find artifact com.sky:sky-common` / `sky-pojo`（兄弟模块尚未安装到本机 Maven 仓库 `~/.m2`），请先在仓库根目录 `sky-take-out` 执行：

```bash
mvn clean install -DskipTests
```

### API 文档

启动后访问 Swagger UI：http://localhost:8080/swagger-ui.html

## 主要功能

### 管理员功能

- ✅ 员工管理（增删改查）
- ✅ 分类管理（菜品分类）
- ✅ 菜品管理（菜品CRUD、图片上传）
- ✅ 套餐管理
- ✅ 订单管理
- ✅ 数据统计

### 用户功能

- ✅ 用户登录（微信小程序）
- ✅ 地址管理
- ✅ 购物车
- ✅ 下单功能
- ✅ 订单查询

## API 接口

### 认证接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/admin/employee/login` | POST | 管理员登录 |
| `/admin/employee/logout` | POST | 管理员退出 |

### 菜品分类接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/admin/category/page` | GET | 分类分页查询 |
| `/admin/category` | POST | 添加分类 |
| `/admin/category/{id}` | PUT | 修改分类 |
| `/admin/category/{id}` | DELETE | 删除分类 |

### 菜品接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/admin/dish/page` | GET | 菜品分页查询 |
| `/admin/dish` | POST | 添加菜品 |
| `/admin/dish/{id}` | PUT | 修改菜品 |
| `/admin/dish/{id}` | DELETE | 删除菜品 |
| `/admin/dish/status/{status}` | POST | 批量启售/停售 |

### 文件上传接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/admin/common/upload` | POST | 文件上传 |

## 配置说明

### application.yml 主要配置

```yaml
server:
  port: 8080                    # 服务端口

spring:
  profiles:
    active: dev                 # 激活的配置文件
  datasource:
    druid:                      # Druid 连接池配置
      url: jdbc:mysql://...
      username: ...
      password: ...

mybatis:
  mapper-locations: classpath:mapper/*.xml  # Mapper XML 位置
  configuration:
    map-underscore-to-camel-case: true       # 驼峰命名映射

sky:
  jwt:
    admin-secret-key: itcast   # JWT 密钥
    admin-ttl: 72000000        # Token 过期时间（毫秒）
    admin-token-name: token    # Token 名称
```

## 开发规范

### 代码风格

- 使用 Lombok 简化代码
- 遵循阿里巴巴 Java 开发手册
- 方法命名使用驼峰命名法
- 常量命名使用大写加下划线

### 日志规范

- 使用 SLF4J 进行日志记录
- 日志级别：DEBUG（开发）、INFO（运行）、WARN（警告）、ERROR（错误）
- 避免在循环中记录日志

### 异常处理

- 使用统一的异常处理器 `GlobalExceptionHandler`
- 自定义业务异常继承 `BaseException`
- 避免直接抛出 RuntimeException

## 部署说明

### 生产环境部署

1. 打包项目：
```bash
mvn clean package
```

2. 运行：
```bash
java -jar sky-server/target/sky-server-1.0.0.jar --spring.profiles.active=prod
```

### Docker 部署

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY sky-server/target/sky-server-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/fooBar`)
3. 提交更改 (`git commit -am 'Add some fooBar'`)
4. 推送到分支 (`git push origin feature/fooBar`)
5. 创建 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱：support@sky-take-out.com
- GitHub Issues：https://github.com/your-repo/issues