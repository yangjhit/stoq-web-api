# 多阶段构建 - 第一阶段：构建应用
FROM maven:3.8.6-openjdk-11 AS builder
# 设置工作目录
WORKDIR /app

# 配置Maven使用阿里云镜像（加速依赖下载）
RUN mkdir -p /root/.m2 && \
    cat > /root/.m2/settings.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyun-central</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Central Repository</name>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
    <mirror>
      <id>aliyun-public</id>
      <mirrorOf>*</mirrorOf>
      <name>Aliyun Public Repository</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
EOF

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 构建应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests -q

# 多阶段构建 - 第二阶段：运行应用
FROM eclipse-temurin:11-jre

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/target/stoq-web-api-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 安装curl用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v3/api-docs || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
