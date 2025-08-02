# PicTech 图片翻译编辑器 - 前后端一体化项目

本项目是一个集成了强大在线图片编辑与多语言翻译功能的全栈解决方案。它由一个 Vue.js 实现的前端编辑器和一个 Spring Boot 构建的后端服务组成。

*   **`frontend/`**: 包含所有前端 Vue 组件和演示应用的源代码。
*   **`src/`**: 包含所有后端 Java Spring Boot 服务的源代码。

![组件效果图](https://your-image-host.com/demo.gif)  <!-- 建议在这里放一张组件的动态效果图 -->

## 🚀 运行演示项目 (Quick Start)

按照以下三个步骤，您可以快速在本地运行完整的前后端演示项目。

### **第 1 步：安装前端依赖**

首先，进入 `frontend` 目录，使用 `npm` 安装所有必需的依赖包。

```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install
```

### **第 2 步：构建前端项目**

安装完依赖后，运行构建命令。这个命令会将 Vue.js 应用打包成静态文件，并根据 `vue.config.js` 的配置，自动将打包好的文件（HTML, CSS, JS）输出到后端 Spring Boot 项目的 `src/main/resources/static` 目录下。

```bash
# 确保你当前在 frontend 目录下
npm run build
```
当您看到构建成功的提示后，前端部分就已经准备就绪了。

### **第 3 步：启动后端服务**

现在，回到项目的根目录，我们来启动后端 Spring Boot 服务。

**A. 环境配置 (首次运行前检查)**

请确保 `src/main/resources/application.properties` 文件中的配置是正确的。

```properties
# 服务器运行端口
server.port=8080

# 【重要】文件上传和输出目录
# 这个目录用于存放所有由程序生成的图片，例如用户上传的原始图、最终导出的成品图等。
# 路径可以是相对路径(如 './output')或服务器上的绝对路径(如 '/var/www/uploads/')。
# 项目启动时会自动创建此目录。
file.upload-dir=./output

# (可选) PicTech API 的凭证，如果您的 service 层需要调用
pictech.api.base-url=http://example.com
pictech.api.key=your_api_key
pictech.api.secret=your_api_secret

# 上传文件大小限制
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

**B. 启动服务**

 
```bash
1.  导航到项目的根目录（即包含 `pom.xml` 文件的目录）。
2.  启动 ImageTranslationApiApplication 
3.  当您在控制台看到类似 `Started ImageTranslationApiApplication in ... seconds` 的日志时，表示后端服务已成功启动。
```

当您在控制台看到 Spring Boot 的启动日志，并显示服务已在端口 `8080` 上运行时，说明后端服务已成功启动。

### **第 4 步：访问应用**

打开您的浏览器，访问：

**http://localhost:8080**

您现在应该能看到并使用功能完整的图片翻译编辑器了！

---

## 📁 项目结构与说明

*   `frontend/`
    *   `public/`: 存放静态资源如 `index.html`。
    *   `src/`: 前端 Vue 应用的核心代码。
        *   `components/ImageEditor.vue`: 核心编辑器组件。
        *   `App.vue`: 用于承载和演示 `ImageEditor` 组件的父组件。
        *   `main.js`: Vue 应用的入口，在此处进行插件注册和配置。
    *   `vue.config.js`: Vue CLI 的配置文件。**已配置**将构建产物直接输出到后端 `static` 目录，实现了前后端一体化部署。

*   `src/main/`
    *   `java/com/pictech/`: 后端 Java 代码。
        *   `controller/`: RESTful API 接口层。
        *   `service/`: 业务逻辑层。
        *   `dto/`: 数据传输对象 (Data Transfer Objects)。
    *   `resources/`:
        *   `static/`: **存放由前端构建出的静态文件。**
        *   `application.properties`: Spring Boot 的核心配置文件。

*   `output/`: (首次运行后自动创建)
    *   由 `file.upload-dir` 配置指定，用于存放所有运行过程中生成的图片文件。

## 📄 API 接口

所有后端接口都定义在 `TranslationController.java` 中，并以 `/api/translate` 作为根路径。

*   `POST /api/translate/upload`: 处理文件上传的翻译任务。
*   `POST /api/translate/url`: 处理基于 URL 的翻译任务。
*   `POST /api/translate/save`: 保存编辑器的当前画布状态。
*   `POST /api/translate/uploadExportedImage`: **接收并保存在前端导出的最终图片**。
*   `GET /api/translate/result/{requestId}`: 查询翻译任务的处理结果。

## 🤝 贡献

如果您有任何改进建议或发现 Bug，欢迎提交 Issue 或 Pull Request。
