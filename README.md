# 数据清洗平台

基于 RuoYi-Vue3 + SpringBoot3 的数据清洗管理平台

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue 3 + Element Plus | 3.5.x |
| 构建工具 | Vite | 6.x |
| 状态管理 | Pinia | 3.x |
| 后端框架 | Spring Boot | 3.5.x |
| ORM | MyBatis-Plus | 3.0.x |
| 数据库 | PostgreSQL / MySQL | - |
| 缓存 | Redis | - |
| 安全 | Spring Security + JWT | - |
| JDK | Java | 17+ |

## 项目结构

```
data-cleaning-platform/
├── RuoYi-Vue3/              # 前端项目
│   ├── src/                 # 源代码
│   ├── public/              # 静态资源
│   ├── vite.config.js       # Vite配置
│   └── package.json
│
├── RuoYi-Vue-springboot3/   # 后端项目
│   ├── ruoyi-admin/         # 主模块（启动类）
│   ├── ruoyi-common/        # 通用模块
│   ├── ruoyi-framework/     # 框架模块
│   ├── ruoyi-generator/     # 代码生成器
│   ├── ruoyi-quartz/        # 定时任务
│   ├── ruoyi-system/        # 系统模块
│   └── sql/                 # 数据库脚本
│
└── 数据清洗平台后端技术方案.md
```

---

## 快速启动

### 1. 后端启动

#### 1.1 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 12+ 或 MySQL 8+
- Redis 6+

#### 1.2 数据库配置

修改 `RuoYi-Vue-springboot3/ruoyi-admin/src/main/resources/application-druid.yml`:

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      # PostgreSQL 配置
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://localhost:5432/ruoyi
      username: postgres
      password: your_password
      # 如果使用 MySQL，注释上面配置，取消下面注释
      # driver-class-name: com.mysql.cj.jdbc.Driver
      # url: jdbc:mysql://localhost:3306/ruoyi?useUnicode=true&characterEncoding=utf8
      # username: root
      # password: your_password
```

#### 1.3 初始化数据库

```bash
# 连接 PostgreSQL/MySQL，创建数据库 ruoyi
# 然后执行 SQL 脚本
psql -U postgres -d ruoyi -f RuoYi-Vue-springboot3/sql/ry_20250522_pg.sql
# 或 MySQL:
# mysql -u root -p ruoyi < RuoYi-Vue-springboot3/sql/ry_20250522.sql
```

#### 1.4 启动后端

```bash
cd RuoYi-Vue-springboot3

# Maven 编译（首次）
mvn clean package -DskipTests

# 启动（开发模式）
mvn spring-boot:run

# 或运行 JAR 包
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

后端启动后访问：`http://localhost:8080`

---

### 2. 前端启动

#### 2.1 环境要求

- Node.js 18+
- Yarn 或 npm

#### 2.2 安装依赖

```bash
cd RuoYi-Vue3

# 使用 yarn（推荐）
yarn install

# 或使用 npm
npm install
```

#### 2.3 配置后端地址

修改 `RuoYi-Vue3/.env.development`:

```env
VITE_APP_BASE_API = 'http://localhost:8080'
VITE_APP_NAME = '数据清洗平台'
```

#### 2.4 启动前端

```bash
# 开发模式
yarn dev

# 或
npm run dev
```

前端启动后访问：`http://localhost:80`（或 Vite 分配的端口）

---

### 3. 登录账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

---

## 常用命令

### 后端

```bash
# 编译打包
mvn clean package -DskipTests

# 只编译
mvn compile

# 运行测试
mvn test
```

### 前端

```bash
# 开发环境
yarn dev

# 构建生产环境
yarn build:prod

# 构建测试环境
yarn build:stage

# 预览打包结果
yarn preview
```

---

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端 API | 8080 | Spring Boot 默认端口 |
| 前端 | 80 | Vite 开发服务器 |
| Swagger | 8080/swagger-ui.html | API 文档 |
| Redis | 6379 | 缓存服务（默认） |
| PostgreSQL | 5432 | 数据库（默认） |
| MySQL | 3306 | 数据库（默认） |

---

## 注意事项

1. **JDK 版本**：后端必须使用 JDK 17+
2. **Redis 配置**：检查 `application.yml` 中的 Redis 配置是否正确
3. **跨域问题**：前后端分离部署时注意 CORS 配置
4. **XSS 过滤**：生产环境建议开启 XSS 防护

---

## 相关文档

- [RuoYi 官方文档](http://doc.ruoyi.vip)
- [Vue 3 文档](https://v3.cn.vuejs.org)
- [Element Plus](https://element-plus.org)
- [Spring Boot 3](https://spring.io/projects/spring-boot)
