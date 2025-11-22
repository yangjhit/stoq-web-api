#!/bin/bash

# 开发环境停止脚本

echo "======================================"
echo "  停止 Stoq Web API"
echo "======================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# 停止服务
echo -e "${YELLOW}🛑 停止服务...${NC}"
docker compose down

echo ""
echo -e "${GREEN}✅ 服务已停止${NC}"
echo ""
echo -e "${CYAN}💡 提示:${NC}"
echo -e "   重新启动: ${YELLOW}./start-dev.sh${NC}"
echo -e "   删除数据: ${YELLOW}docker compose down -v${NC}"
echo ""
