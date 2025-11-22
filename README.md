# Stoq Web API

基于 Spring Boot 的仓库管理系统 API 服务。

---

## 🚀 快速启动

### 服务器部署（推荐）

```bash
# 1. 配置环境变量
cp .env.prod.example .env.prod
vim .env.prod  # 修改所有密码

# 2. 启动服务（自动安全检查）
./start-prod.sh

# 3. 访问应用
curl http://localhost:8080/api/v3/api-docs
```

### 本地开发

```bash
# 1. 启动服务（自动构建 JAR）
./start-dev.sh

# 2. 访问应用
open http://localhost:8080/api/swagger-ui.html
```

---

## 📝 常用命令

### 开发环境

```bash
# 启动
./start-dev.sh

# 停止
./stop-dev.sh

# 查看日志
docker compose logs -f app

# 重启应用
docker compose restart app
```

### 生产环境

```bash
# 启动（含安全检查）
./start-prod.sh

# 停止（可选备份）
./stop-prod.sh

# 重启应用
./restart-prod.sh -a

# 重新构建
./restart-prod.sh -b

# 查看日志
docker compose -f docker-compose.prod.yml logs -f app
```

---

## 🔧 开发流程

### 修改代码后

```bash
# 1. 重新构建
mvn clean package -DskipTests

# 2. 重启应用
docker compose restart app
```

### 停止服务

```bash
docker compose down
```

---

## 🌐 访问地址

- **API 文档**: http://localhost:8080/api/swagger-ui.html
- **API 端点**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/api/v3/api-docs

---

## 📊 服务端口

| 服务 | 端口 |
|------|------|
| 应用 | 8080 |
| MySQL | 3306 |
| Redis | 6379 |

---

## 📚 API 文档

- [用户登录指南](doc/用户登录指南.md) - 用户认证 API
- [公司和仓库管理接口指南](doc/公司和仓库管理接口指南.md) - 业务 API
- [商品分类和模板接口指南](doc/商品分类和模板接口指南.md) - 商品 API

---

## 🔧 技术栈

- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **认证**: JWT
- **文档**: OpenAPI 3.0 (Swagger)

---

## 📁 项目结构

```
stoq-web-api-new/
├── src/main/java/com/stoq/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── dto/             # 数据传输对象
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   └── util/            # 工具类
├── docker-compose.yml   # Docker 编排配置
├── Dockerfile.local     # Docker 镜像配置
└── pom.xml              # Maven 配置
```

---

## 🐛 故障排查

### 端口被占用

```bash
# 查看占用端口的进程
lsof -i :8080
lsof -i :3306
lsof -i :6379

# 停止服务
docker compose down
```

### 应用无法启动

```bash
# 查看日志
docker compose logs -f app

# 重新构建
mvn clean package -DskipTests
docker compose up -d --build
```

### 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker compose ps mysql

# 查看 MySQL 日志
docker compose logs mysql
```

---

## 📖 详细文档

- [服务器部署说明.md](服务器部署说明.md) - 服务器部署指南 ⭐
- [成功启动总结.md](成功启动总结.md) - 本地开发指南

---

## 📄 License

MIT License

---

**最后更新**: 2025-11-22
