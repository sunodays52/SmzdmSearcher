# 构建 APK 指南

## 方法一：使用 GitHub Actions（推荐，无需本地环境）

### 步骤
1. **上传项目到 GitHub**
   ```bash
   # 在 smzdm-searcher 目录下执行
   git init
   git add .
   git commit -m "init: 什么值得买自动搜索工具"
   ```

2. **在 GitHub 创建仓库**（如 `SmzdmSearcher`）
   ```bash
   git remote add origin https://github.com/你的用户名/SmzdmSearcher.git
   git push -u origin main
   ```

3. **触发自动构建**
   - 进入 GitHub 仓库页面
   - 点击 **Actions** → **Build APK** → **Run workflow**
   - 等待约 5-10 分钟
   - 构建完成后，在 Summary 页面下载 `smzdm-searcher-debug.zip`

4. **安装到手机**
   - 解压下载的 zip
   - 找到 `app-debug.apk`
   - 传到小米 15 Pro 上安装
   - 如果提示「未知来源」，在设置中允许安装未知应用

---

## 方法二：使用 Android Studio（本地构建）

### 前置条件
- 安装 [Android Studio 2024.x](https://developer.android.com/studio)
- 安装 JDK 17（Android Studio 自带）

### 步骤
1. **打开项目**
   - 启动 Android Studio
   - 选择 **Open an existing project**
   - 选择 `smzdm-searcher` 文件夹
   - 等待 Gradle sync 完成（首次会下载依赖，可能需要网络）

2. **构建 APK**
   - 菜单栏: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
   - 或者点击右侧 Gradle 面板: `app` → `Tasks` → `build` → `assembleDebug`

3. **获取 APK**
   - 构建完成后，底部会弹出通知，点击 **locate** 即可找到
   - APK 路径: `app/build/outputs/apk/debug/app-debug.apk`

4. **安装到小米 15 Pro**
   - 用 USB 数据线连接手机
   - 手机上开启 **开发者选项** → **USB 调试**
   - 在 Android Studio 中直接点击 Run ▶ 按钮
   - 或直接把 APK 复制到手机安装

---

## 安装到小米 15 Pro（澎湃OS 2.0）

### 首次安装准备
1. 打开 **设置** → **安全** → **更多安全设置** → **安装未知应用**
2. 找到你的文件管理器，允许「安装未知应用」
3. 或者使用小米自带的「文件管理」直接安装 APK

### 授予权限
安装后打开应用，在「设置」Tab 中：
1. **通知权限** → 点击「去授权」→ 开启通知
2. **通知监听权限** → 点击「去授权」→ 找到「什么值得买搜索」→ 开启
3. **电池优化** → 点击「去授权」→ 选择「不优化」

### 开始使用
1. 切换到「监控列表」Tab
2. 点击右下角 **+** 按钮
3. 输入想监控的商品关键词（如：牛奶、硬盘、咖啡、奶粉）
4. 等待 15 分钟，系统会自动搜索并推送通知

---

## 问题排查

| 问题 | 解决方法 |
|------|---------|
| 构建失败: SDK not found | 在 Android Studio 中安装 SDK Platform 35 |
| 构建失败: Gradle sync 慢 | 使用代理或等待，首次需要下载依赖 |
| 收不到通知 | 检查「设置」Tab 中的通知权限是否开启 |
| 搜索结果为空 | 检查网络连接，smzdm API 可能需要翻墙 |
| APK 安装失败 | 在「设置」→「安全」中开启「允许安装未知来源应用」 |
