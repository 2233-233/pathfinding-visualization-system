# 第一阶段：构建 - 使用3.8.5版本避免exec format error
FROM maven:3.8.5-openjdk-11 AS builder

# 配置阿里云 Maven 镜像（解决网络慢/依赖下载失败）
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

WORKDIR /app

# 复制 pom.xml 并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 打包应用
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM openjdk:11-jre-slim

WORKDIR /app

# 复制 JAR 文件（通配符匹配）
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]