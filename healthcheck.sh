#!/bin/bash

# 健康检查脚本
echo "======================================"
echo "  Stoq Web API - 健康检查"
echo "======================================"
echo ""

# 显示容器状态
echo "📊 容器状态:"
docker compose ps
echo ""

# 检查MySQL
echo "📊 MySQL状态:"
# 从环境变量或使用默认密码
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root_password_123}
docker compose exec -T mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD} 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ MySQL运行正常"
else
    echo "❌ MySQL无法访问"
fi
echo ""

# 检查Redis
echo "📊 Redis状态:"
docker compose exec -T redis redis-cli ping 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Redis运行正常"
else
    echo "❌ Redis无法访问"
fi
echo ""

# 检查应用
echo "📊 应用状态:"
curl -s http://localhost:8080/api/v3/api-docs > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ 应用运行正常"
    echo "   API文档: http://localhost:8080/api/swagger-ui.html"
else
    echo "❌ 应用无法访问"
fi
