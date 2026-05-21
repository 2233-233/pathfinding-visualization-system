#!/bin/bash

# Redis集成测试脚本
# 用于测试Redis配置和缓存服务

echo "🚀 开始Redis集成测试..."
echo "========================================"

# 1. 检查Redis服务是否运行
echo "1. 检查Redis服务状态..."
redis-cli ping
if [ $? -eq 0 ]; then
    echo "✅ Redis服务正常运行"
else
    echo "❌ Redis服务未运行，请先启动Redis"
    echo "    Windows: 打开Docker Desktop，启动Redis容器"
    echo "    Linux/Mac: sudo systemctl start redis"
    exit 1
fi

echo ""
echo "2. 测试Redis基本连接..."
redis-cli set test:connection "Hello Redis"
redis-cli get test:connection
redis-cli del test:connection

echo ""
echo "3. 查看Redis信息..."
redis-cli info server | grep -E "(redis_version|uptime_in_days|connected_clients)"
redis-cli info memory | grep -E "(used_memory_human|maxmemory_human)"

echo ""
echo "4. 查看Redis键空间..."
redis-cli info keyspace

echo ""
echo "========================================"
echo "📊 Redis集成测试完成！"
echo ""
echo "下一步操作："
echo "1. 启动Spring Boot应用: mvn spring-boot:run"
echo "2. 运行单元测试: mvn test -Dtest=RedisIntegrationTest"
echo "3. 访问Swagger文档: http://localhost:8081/swagger-ui.html"
echo "4. 测试登录/登出功能，验证Token缓存"
echo ""
echo "📚 学习要点总结："
echo "   - Redis作为内存数据库，提供高性能缓存"
echo "   - Token黑名单实现安全的JWT注销"
echo "   - 用户会话缓存减少数据库查询"
echo "   - 算法结果缓存提高系统响应速度"
echo "   - Redis数据结构（String、List、Set、Hash）的应用场景"