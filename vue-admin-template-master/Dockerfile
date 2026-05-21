# 使用Node.js的官方Alpine镜像，体积更小
FROM node:18-alpine

# 设置容器内的工作目录
WORKDIR /app

# 先复制依赖文件，利用Docker缓存机制
COPY package*.json ./

# 安装项目依赖
RUN npm install

# 复制所有源代码到工作目录
COPY . .

# 暴露开发服务器运行的端口（通常是5173, 3000等，请根据你的项目修改）
EXPOSE 9528


# 容器启动时运行的命令，启动开发服务器
CMD ["npm", "run", "dev"]
