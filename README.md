# 🌸 汐汐的小窝 (xixi-baby-world)

宝宝成长记录平台，一个给家人们共同记录汐汐成长的私有空间。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite + Element Plus + Pinia |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring Security |
| 存储 | MySQL 8 + Redis 7 + MinIO |
| AI | Deepseek API（汐汐小助手） |

## 项目结构

```
xixi-baby-world/
├── xixi-backend/        # Spring Boot 后端
│   └── src/main/
│       ├── java/com/xixi/
│       │   ├── module/  # auth / user / baby / home / media / diary / ai / admin
│       │   ├── config/  # Security / MinIO / CORS / MyBatisPlus
│       │   ├── common/  # 统一响应 / 全局异常
│       │   └── util/    # JWT / 验证码 / Redis Keys
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           └── init.sql
└── xixi-frontend/       # Vue 3 前端
    └── src/
        ├── api/         # Axios 接口封装
        ├── stores/      # Pinia 状态管理
        ├── router/      # Vue Router + 路由守卫
        ├── layouts/     # MainLayout（含 AI 悬浮球）
        ├── components/  # FloatingAI
        └── views/       # auth / home / media / diary / profile / admin
```

## 快速启动

### 前置条件

- Java 17+, Maven 3.8+
- MySQL 8.x
- Redis 7.x
- MinIO（或兼容 S3 的存储）
- Node.js 18+

### 后端

```bash
# 1. 执行数据库初始化脚本
mysql -u root -p < xixi-backend/src/main/resources/init.sql

# 2. 修改配置
vim xixi-backend/src/main/resources/application-dev.yml
# 填入: 数据库密码、MinIO 配置、邮件配置、AI API Key

# 3. 启动
cd xixi-backend
mvn spring-boot:run
# 服务运行在 http://localhost:8080
```

### 前端

```bash
cd xixi-frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

### 默认账号

| 账号 | 密码 | 角色 |
|---|---|---|
| admin | Admin@123 | 管理员 |

## 主要功能

- 🌸 **首页** — 宝宝信息展示、小短句轮播（4s）、精选照片、快捷入口
- 📷 **照片墙** — 瀑布流上传/浏览，管理员可设为精选
- 🎬 **视频集** — 视频上传，MinIO 预签名 URL 播放
- 📝 **成长日记** — 富文本（Quill）编辑，心情标签，月份筛选
- 🤖 **汐汐小助手** — 右下角 AI 悬浮球，接入 Deepseek，多轮对话
- 👤 **个人中心** — 头像上传、角色选择、密码修改
- ⚙️ **管理后台** — 宝宝信息 / 用户管理 / 小短句 / SMTP & AI 配置

## 安全特性

- JWT 无状态认证（7天有效期，自动续期）
- 图形验证码登录防暴力破解（失败5次锁定15分钟）
- 邮箱验证码注册（60s 限流）
- Spring Security 路由权限控制
- MinIO 预签名 URL（视频防盗链）
