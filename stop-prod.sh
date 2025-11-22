#!/bin/bash
# 生产环境停止脚本

echo "======================================"
echo "  停止 Stoq Web API - 生产环境"
echo "======================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

# 检查是否有运行的容器
if ! docker compose -f docker-compose.prod.yml ps --services --filter "status=running" 2>/dev/null | grep -q .; then
    echo -e "${YELLOW}⚠️  没有运行中的生产环境服务${NC}"
    exit 0
fi

# 显示当前运行的服务
echo -e "${CYAN}📊 当前运行的服务:${NC}"
docker compose -f docker-compose.prod.yml ps
echo ""

# 显示资源使用情况
echo -e "${CYAN}💻 当前资源使用:${NC}"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}" $(docker compose -f docker-compose.prod.yml ps -q) 2>/dev/null || echo "无法获取资源使用情况"
echo ""

# 警告提示
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${RED}⚠️  警告: 即将停止生产环境服务${NC}"
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 询问是否备份数据库
read -p "是否在停止前备份数据库? (y/n): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # 加载环境变量
    if [ -f .env.prod ]; then
        export $(cat .env.prod | grep -v '^#' | xargs)
    fi
    
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    echo -e "${YELLOW}📦 备份数据库到 ${BACKUP_FILE}...${NC}"
    
    docker compose -f docker-compose.prod.yml exec -T mysql mysqldump \
        -u root \
        -p${MYSQL_ROOT_PASSWORD} \
        ${MYSQL_DATABASE} > ${BACKUP_FILE} 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 数据库备份成功: ${BACKUP_FILE}${NC}"
        echo -e "${CYAN}   文件大小: $(du -h ${BACKUP_FILE} | cut -f1)${NC}"
    else
        echo -e "${RED}❌ 数据库备份失败${NC}"
        read -p "是否继续停止服务? (y/n): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo -e "${RED}❌ 已取消停止${NC}"
            exit 1
        fi
    fi
    echo ""
fi

# 询问是否删除数据卷
REMOVE_VOLUMES=false
if [ "$1" == "--volumes" ] || [ "$1" == "-v" ]; then
    echo -e "${RED}⚠️  警告: 将删除所有数据卷（包括数据库数据）${NC}"
    echo -e "${RED}⚠️  这将永久删除所有数据！${NC}"
    echo ""
    read -p "确认删除数据卷? 请输入 'DELETE' 确认: " CONFIRM
    if [ "$CONFIRM" == "DELETE" ]; then
        REMOVE_VOLUMES=true
    else
        echo -e "${YELLOW}⚠️  已取消删除数据卷${NC}"
        REMOVE_VOLUMES=false
    fi
    echo ""
fi

# 最后确认
read -p "确认停止生产环境服务? (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ 已取消停止${NC}"
    exit 1
fi

# 停止服务
echo ""
echo -e "${YELLOW}🛑 停止生产环境服务...${NC}"

if [ "$REMOVE_VOLUMES" = true ]; then
    docker compose -f docker-compose.prod.yml down -v
    echo ""
    echo -e "${GREEN}✅ 服务已停止，数据卷已删除${NC}"
    echo -e "${RED}⚠️  所有数据已被永久删除${NC}"
else
    docker compose -f docker-compose.prod.yml down
    echo ""
    echo -e "${GREEN}✅ 服务已停止，数据卷已保留${NC}"
fi

echo ""
echo -e "${CYAN}💡 提示:${NC}"
echo -e "   重新启动: ${YELLOW}./start-prod.sh${NC}"
echo -e "   删除数据卷: ${YELLOW}./stop-prod.sh --volumes${NC}"
echo -e "   查看所有容器: ${YELLOW}docker ps -a${NC}"
echo -e "   清理未使用资源: ${YELLOW}docker system prune${NC}"
echo ""

# 如果有备份文件，显示备份信息
if [ -n "$BACKUP_FILE" ] && [ -f "$BACKUP_FILE" ]; then
    echo -e "${CYAN}📦 数据库备份:${NC}"
    echo -e "   文件: ${GREEN}${BACKUP_FILE}${NC}"
    echo -e "   大小: $(du -h ${BACKUP_FILE} | cut -f1)"
    echo -e "   恢复命令: ${YELLOW}docker compose -f docker-compose.prod.yml exec -T mysql mysql -u root -p\${MYSQL_ROOT_PASSWORD} \${MYSQL_DATABASE} < ${BACKUP_FILE}${NC}"
    echo ""
fi
