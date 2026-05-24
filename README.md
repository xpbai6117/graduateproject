# 基于AI大模型的智能记账助手

> 重庆人文科技学院 · 2025届毕业设计

## 项目简介

本项目探索将大语言模型（ChatGPT / 讯飞星火）应用于日常记账场景，用户通过**自然语言输入、语音识别、Apple 快捷指令**等方式描述消费行为，AI 自动解析并分类记录。涵盖微信小程序端、Java 后端、AI 大模型接入等完整技术链路。

## 模块结构

| 目录 | 说明 | 技术栈 |
|------|------|--------|
| `xiaoyi-main/` | **小易智能记账**（主项目）— 多模块 Spring Boot 后端 | Java 17, Spring Boot, JPA, Redis, MySQL, ChatGPT API |
| `xiaoyi-res/` | 小易前端 — UniApp + Vue 3 微信小程序 | Vue 3, Pinia, Vite, UniApp |
| `account/` | **随手记账** — Spring Boot 后端 + 原生微信小程序 | Java, Spring Boot, 微信原生开发 |
| `havefish-bill-wx-master/` | **有鱼账本** — 全栈记账小程序 | Spring Boot, JPA, Redis, Lin UI |
| `bigmodel-java/` | 讯飞星火大模型接入 Demo | Java, 讯飞星火 API |
| `xunfei/` | 讯飞相关微信小程序 | 微信小程序原生 |

## 核心功能

- **AI 智能记账** — 自然语言输入（文字/语音），大模型自动识别金额、类别、时间
- **语音识别** — 接入华为 SIS 语音识别，支持语音记账
- **快捷指令** — 支持 Apple Shortcuts，一键快速记账
- **预算管理** — 月度预算设置、超支提醒
- **可视化统计** — 收支图表、分类占比、趋势分析
- **微信登录** — 微信授权登录，多用户管理

## 技术架构

```
┌─────────────────────────────────┐
│     微信小程序 / UniApp          │  前端层
├─────────────────────────────────┤
│   Spring Boot 多模块后端         │  服务层
│   ├─ admin (管理后台)            │
│   ├─ mini (小程序接口)           │
│   ├─ gpt (AI 对话)              │
│   ├─ wechat (微信对接)           │
│   ├─ member (用户模块)           │
│   └─ ledger (账本模块)           │
├─────────────────────────────────┤
│   MySQL + Redis + JPA           │  数据层
├─────────────────────────────────┤
│   ChatGPT / 讯飞星火 / 华为SIS   │  AI 层
└─────────────────────────────────┘
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis
- Node.js 16+（前端）

### 后端启动

```bash
cd xiaoyi-main
mvn clean install -DskipTests
# 修改各模块 application.yml 中的数据库/AI配置后启动
```

### 前端启动

```bash
cd xiaoyi-res
npm install
# 使用 HBuilderX 或微信开发者工具打开运行
```

## 相关文档

- 开题报告、任务书等毕业设计文档见根目录

## License

仅供学习交流使用。
