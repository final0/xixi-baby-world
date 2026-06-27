# 汐汐的小窝 - 前端启动指南

## 安装依赖

```bash
npm install
```

## 开发调试

```bash
npm run dev
```

> 默认运行在 http://localhost:5173， API 请求代理到 http://localhost:8080

## 生产构建

```bash
npm run build
# 输出到 dist/ 目录，由 Nginx 托管
```

## 目录结构

```
src/
├── api/          # 接口封装 (Axios)
├── assets/       # 静态资源
├── components/   # 公共组件 (FloatingAI)
├── layouts/      # 布局组件
├── router/       # 路由配置
├── stores/       # Pinia 状态管理
└── views/        # 页面组件
    ├── auth/         登录/注册/角色选择
    ├── home/         首页
    ├── media/        照片墙/视频集
    ├── diary/        成长日记
    ├── profile/      个人中心
    └── admin/        管理后台
```
