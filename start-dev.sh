#!/bin/bash

# 开发环境启动脚本（本地开发专用）

echo "======================================"
echo "  启动 Stoq Web API - 开发环境"
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

# 检查 JAR 文件
if [ ! -f "target/stoq-web-api-1.0.0.jar" ]; then
    echo -e "${YELLOW}📦 未找到 JAR 文件，正在构建...${NC}"
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ 构建失败${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ 构建成功${NC}"
    echo ""
fi

# 临时修改 docker-compose.yml 使用 Dockerfile.local
echo -e "${CYAN}🔧 配置本地开发环境...${NC}"
sed -i.bak 's/dockerfile: Dockerfile$/dockerfile: Dockerfile.local/' docker-compose.yml

# 启动服务
echo -e "${YELLOW}🚀 启动服务...${NC}"
docker compose up -d

# 恢复 docker-compose.yml
mv docker-compose.yml.bak docker-compose.yml

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 启动失败${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}✅ 服务已启动${NC}"
echo ""
echo -e "${CYAN}📍 访问地址:${NC}"
echo -e "   🌐 API 文档: ${GREEN}http://localhost:8080/api/swagger-ui.html${NC}"
echo -e "   🔌 API 端点: ${GREEN}http://localhost:8080/api${NC}"
echo ""
echo -e "${CYAN}📝 常用命令:${NC}"
echo -e "   查看日志: ${YELLOW}docker compose logs -f app${NC}"
echo -e "   停止服务: ${YELLOW}./stop-dev.sh${NC}"
echo -e "   重启应用: ${YELLOW}docker compose restart app${NC}"
echo ""
echo -e "${CYAN}💡 提示:${NC}"
echo -e "   本地开发使用 Dockerfile.local（避免网络问题）"
echo -e "   服务器部署使用 Dockerfile（标准多阶段构建）"
echo ""
