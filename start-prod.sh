#!/bin/bash
# 生产环境启动脚本

echo "======================================"
echo "  启动 Stoq Web API - 生产环境"
echo "======================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

# 检查 Docker
if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Docker 未运行，请先启动 Docker${NC}"
    exit 1
fi

# 检查配置文件
if [ ! -f ".env.prod" ]; then
    echo -e "${RED}❌ 未找到 .env.prod 配置文件${NC}"
    echo -e "${CYAN}请执行以下步骤:${NC}"
    echo "  1. cp .env.prod.example .env.prod"
    echo "  2. vim .env.prod  # 修改所有密码和密钥"
    echo "  3. ./start-prod.sh"
    exit 1
fi

# 加载环境变量
export $(cat .env.prod | grep -v '^#' | xargs)

# 安全检查
echo -e "${CYAN}🔒 安全检查...${NC}"
SECURITY_ISSUES=0

# 检查 MySQL root 密码
if [ "$MYSQL_ROOT_PASSWORD" == "CHANGE_THIS_TO_STRONG_PASSWORD" ]; then
    echo -e "${RED}   ⚠️  MySQL root 密码使用了默认值${NC}"
    SECURITY_ISSUES=$((SECURITY_ISSUES + 1))
fi

# 检查 MySQL 用户密码
if [ "$MYSQL_PASSWORD" == "CHANGE_THIS_TO_STRONG_PASSWORD" ]; then
    echo -e "${RED}   ⚠️  MySQL 用户密码使用了默认值${NC}"
    SECURITY_ISSUES=$((SECURITY_ISSUES + 1))
fi

# 检查 Redis 密码
if [ "$SPRING_REDIS_PASSWORD" == "CHANGE_THIS_TO_REDIS_PASSWORD" ]; then
    echo -e "${RED}   ⚠️  Redis 密码使用了默认值${NC}"
    SECURITY_ISSUES=$((SECURITY_ISSUES + 1))
fi

# 检查 JWT 密钥
if [[ "$JWT_SECRET" == *"CHANGE_THIS"* ]]; then
    echo -e "${RED}   ⚠️  JWT 密钥使用了默认值${NC}"
    SECURITY_ISSUES=$((SECURITY_ISSUES + 1))
fi

# 检查 JWT 密钥长度
if [ ${#JWT_SECRET} -lt 64 ]; then
    echo -e "${RED}   ⚠️  JWT 密钥长度不足（建议至少 64 字符）${NC}"
    SECURITY_ISSUES=$((SECURITY_ISSUES + 1))
fi

# 检查邮件配置
if [ -z "$SPRING_MAIL_USERNAME" ] || [ -z "$SPRING_MAIL_PASSWORD" ]; then
    echo -e "${YELLOW}   ⚠️  邮件服务未配置${NC}"
fi

if [ $SECURITY_ISSUES -gt 0 ]; then
    echo ""
    echo -e "${RED}❌ 发现 $SECURITY_ISSUES 个安全问题${NC}"
    echo -e "${YELLOW}生产环境必须修改所有默认密码和密钥！${NC}"
    echo ""
    read -p "是否忽略安全警告继续启动? (y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${RED}❌ 已取消启动${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✅ 安全检查通过${NC}"
fi

echo ""

# 显示配置信息
echo -e "${CYAN}📋 配置信息:${NC}"
echo -e "   数据库: ${GREEN}${MYSQL_DATABASE}${NC}"
echo -e "   应用端口: ${GREEN}8080${NC}"
echo -e "   MySQL 端口: ${GREEN}3306${NC}"
echo -e "   Redis 端口: ${GREEN}6379${NC}"
echo ""

# 确认启动
echo -e "${YELLOW}⚠️  生产环境部署确认${NC}"
read -p "确认启动生产环境? (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ 已取消启动${NC}"
    exit 1
fi

# 启动服务
echo ""
echo -e "${YELLOW}🚀 启动生产环境服务...${NC}"
echo -e "${CYAN}   - MySQL 8.0${NC}"
echo -e "${CYAN}   - Redis 7${NC}"
echo -e "${CYAN}   - Spring Boot 应用${NC}"
echo ""

docker compose -f docker-compose.prod.yml up -d --build

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}❌ 启动失败${NC}"
    echo -e "${CYAN}💡 查看错误日志: docker compose -f docker-compose.prod.yml logs${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}⏳ 等待服务启动...${NC}"

# 等待 MySQL
echo -n "   等待 MySQL 启动"
for i in {1..30}; do
    if docker compose -f docker-compose.prod.yml exec -T mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD} 2>/dev/null | grep -q "mysqld is alive"; then
        echo -e " ${GREEN}✅${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 等待 Redis
echo -n "   等待 Redis 启动"
for i in {1..15}; do
    if docker compose -f docker-compose.prod.yml exec -T redis redis-cli -a ${SPRING_REDIS_PASSWORD} ping 2>/dev/null | grep -q "PONG"; then
        echo -e " ${GREEN}✅${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 等待应用
echo -n "   等待应用启动"
for i in {1..60}; do
    if curl -s http://localhost:8080/api/v3/api-docs > /dev/null 2>&1; then
        echo -e " ${GREEN}✅${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

echo ""
echo -e "${GREEN}✅ 生产环境启动成功！${NC}"
echo ""
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}📍 访问地址:${NC}"
echo -e "   🌐 API 文档: ${GREEN}http://localhost:8080/api/swagger-ui.html${NC}"
echo -e "   🔌 API 端点: ${GREEN}http://localhost:8080/api${NC}"
echo ""
echo -e "${CYAN}📝 常用命令:${NC}"
echo -e "   查看日志: ${YELLOW}docker compose -f docker-compose.prod.yml logs -f app${NC}"
echo -e "   停止服务: ${YELLOW}./stop-prod.sh${NC}"
echo -e "   重启应用: ${YELLOW}docker compose -f docker-compose.prod.yml restart app${NC}"
echo -e "   查看状态: ${YELLOW}docker compose -f docker-compose.prod.yml ps${NC}"
echo ""
echo -e "${CYAN}🔒 安全提醒:${NC}"
echo -e "   - 建议配置 HTTPS（使用 Nginx 反向代理）"
echo -e "   - 定期备份数据库"
echo -e "   - 监控应用日志和资源使用"
echo -e "   - 及时更新依赖和镜像"
echo ""
echo -e "${CYAN}💾 数据库备份:${NC}"
echo -e "   ${YELLOW}docker compose -f docker-compose.prod.yml exec mysql mysqldump -u root -p\${MYSQL_ROOT_PASSWORD} \${MYSQL_DATABASE} > backup_\$(date +%Y%m%d).sql${NC}"
echo ""
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
