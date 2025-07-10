# 二维码登录系统文档

本项目是一个基于 Vue 3 和 Spring Boot 的二维码登录系统，支持移动端和 Web 端的扫码登录功能。

## 技术栈

- **前端**: Vue 3 + Vite + WebSocket
- **后端**: Spring Boot + Redis + WebSocket
- **其他技术**: QRCode 生成、跨域配置、RESTful API 设计

---

## 功能说明

### 1. 二维码生成与刷新
- 用户在 Web 端点击“扫码登录”按钮后，后端生成唯一的二维码 ID。
- 使用 `QRCodeUtil` 生成二维码图片并返回给前端展示。
- 支持二维码刷新功能，重新生成新的二维码 ID 并更新页面。

### 2. 移动端扫码登录
- 用户使用移动端（如 App 或浏览器）访问 `/mobile/login` 页面。
- 输入用户名或选择用户后，扫描二维码完成身份验证。
- 扫描成功后通过 WebSocket 实时通知 Web 端登录状态。

### 3. 登录状态同步
- 登录状态通过 WebSocket 实时推送，避免轮询请求。
- Web 端监听状态变化后自动跳转到登录成功页面。

### 4. Redis 缓存管理
- 使用 Redis 缓存二维码状态和用户登录信息，提升性能。
- 设置合理的过期时间防止缓存堆积。

---
---

## WebSocket 通信

- 地址：`ws://localhost:8080/ws/qrcode/{uuid}`
- 用于 Web 端监听二维码状态变化。
- 消息格式：
~~~json 
{
  "status": "SCANED", 
  "username": "testuser"
}

~~~



