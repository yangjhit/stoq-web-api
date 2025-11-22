#!/bin/bash
# 生产环境重启脚本

echo "======================================"
echo "  重启 Stoq Web API - 生产环境"
echo "======================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

# 警告提示
echo -e "${RED}⚠️  警告: 即将重启生产环境服务${NC}"
echo ""
read -p "确认重启生产环境? (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ 已取消重启${NC}"
    exit 1
fi

echo ""

# 检查参数
if [ "$1" == "--app" ] || [ "$1" == "-a" ]; then
    echo -e "${YELLOW}🔄 仅重启应用服务...${NC}"
    docker compose -f docker-compose.prod.yml restart app
    echo ""
    echo -e "${GREEN}✅ 应用服务已重启${NC}"
    echo -e "${CYAN}💡 查看日志: docker compose -f docker-compose.prod.yml logs -f app${NC}"
elif [ "$1" == "--build" ] || [ "$1" == "-b" ]; then
    echo -e "${YELLOW}🔄 重新构建并重启...${NC}"
    ./stop-prod.sh
    if [ $? -eq 0 ]; then
        ./start-prod.sh
    fi
else
    echo -e "${YELLOW}🔄 重启所有服务...${NC}"
    docker compose -f docker-compose.prod.yml restart
    echo ""
    echo -e "${GREEN}✅ 所有服务已重启${NC}"
    echo -e "${CYAN}💡 查看日志: docker compose -f docker-compose.prod.yml logs -f${NC}"
fi

echo ""
