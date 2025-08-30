# PicTech 图片翻译编辑器 - 前后端一体化项目

本项目是一个集成了强大在线图片编辑与多语言翻译功能的全栈解决方案。它由 Vue.js 实现的前端编辑器和 Spring Boot 构建的后端服务组成。

* **`frontend/`**: 包含所有前端 Vue 组件和演示应用的源代码。
* **`src/`**: 包含所有后端 Java Spring Boot 服务的源代码。


## 🚀 运行演示项目 (快速开始)

按照以下步骤，您可以快速在本地运行完整的前后端演示项目。

### **第 1 步：安装前端依赖**

首先，进入 `frontend` 目录，使用 `npm` 安装所有必需的依赖包。

```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install
```

### **第 2 步：构建前端项目**

安装完依赖后，运行构建命令。此命令会将 Vue.js 应用打包成静态文件，并根据 `vue.config.js` 的配置，自动将打包好的文件（HTML、CSS、JS）输出到后端 Spring Boot 项目的 `src/main/resources/static` 目录下。

```bash
# 确保当前在 frontend 目录下
npm run build
```

当您看到构建成功的提示后，前端部分已准备就绪。

### **第 3 步：启动后端服务**

现在，回到项目根目录，启动后端 Spring Boot 服务。

**A. 环境配置 (首次运行前检查)**

请确保 `src/main/resources/application.properties` 文件中的配置正确。

```properties
# 服务器运行端口
server.port=8080

# 【重要】文件上传和输出目录
# 此目录用于存放程序生成的所有图片，如用户上传的原始图、最终导出的成品图等。
# 路径可以是相对路径（如 './output'）或服务器上的绝对路径（如 '/var/www/uploads/'）。
# 项目启动时会自动创建此目录。
file.upload-dir=./output

# (可选) PicTech API 的凭证，若 service 层需要调用
pictech.api.base-url=http://example.com
pictech.api.key=your_api_key
pictech.api.secret=your_api_secret

# 上传文件大小限制
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

**B. 启动服务**

```bash
1. 导航到项目根目录（包含 `pom.xml` 文件的目录）。
2. 启动 ImageTranslationApiApplication。
3. 当控制台显示类似 `Started ImageTranslationApiApplication in ... seconds` 的日志时，表示后端服务已成功启动。
```

当控制台显示 Spring Boot 启动日志，并表明服务已在端口 `8080` 运行时，后端服务已成功启动。

### **第 4 步：访问应用**

打开浏览器，访问：

**http://localhost:8080**

您现在应该可以看到并使用功能完整的图片翻译编辑器！

---

## 📁 项目结构与说明

* **`frontend/`**
    * `public/`：存放静态资源，如 `index.html`。
    * `src/`：前端 Vue 应用的核心代码。
        * `components/ImageEditor.vue`：核心编辑器组件，负责图片编辑和翻译功能。
        * `App.vue`：用于承载和演示 `ImageEditor` 组件的父组件。
            * **按钮配置**：`App.vue` 的 `data` 中包含 `myButtonConfig` 对象，用于控制编辑器界面中按钮的显示与隐藏，配置如下：
                ```javascript
                myButtonConfig: {
                    upload: true,    // 启用文件上传按钮
                    addText: true,   // 启用添加文本按钮
                    restore: true,   // 启用局部恢复按钮
                    undo: true,      // 启用撤销按钮
                    redo: true,      // 启用重做按钮
                    erase: true,     // 启用擦除按钮
                    export: true,    // 启用导出按钮
                    save: true,      // 启用保存按钮
                    reset: true      // 启用重置按钮
                }
                ```
              通过将这些选项设置为 `true` 或 `false`，可以控制编辑器界面中相应按钮的显示或隐藏，以实现个性化的用户体验。
        * `main.js`：Vue 应用的入口，用于插件注册和配置。
    * `vue.config.js`：Vue CLI 的配置文件，已配置将构建产物输出到后端 `static` 目录，实现前后端一体化部署。

* **`src/main/`**
    * `java/com/pictech/`：后端 Java 代码。
        * `controller/`：RESTful API 接口层。
        * `service/`：业务逻辑层。
        * `dto/`：数据传输对象（DTOs）。
    * `resources/`：
        * `static/`：存放前端构建生成的静态文件。
        * `application.properties`：Spring Boot 核心配置文件。

* **`output/`**：（首次运行后自动创建）
    * 由 `file.upload-dir` 配置指定，用于存放运行过程中生成的所有图片文件。

## 📄 API 接口

所有后端接口定义在 `TranslationController.java` 中，根路径为 `/api/translate`。

* `POST /api/translate/upload`：处理文件上传的翻译任务。
* `POST /api/translate/url`：处理基于 URL 的翻译任务。
* `POST /api/translate/save`：保存编辑器当前画布状态。
* `POST /api/translate/uploadExportedImage`：**接收并保存前端导出的最终图片，建议定期清理**。
* `GET /api/translate/result/{requestId}`：查询翻译任务的处理结果。
* `POST /api/translate/iopaint`：请求擦除服务，消耗一个积分。
* `POST /api/translate/uploadIoInpaintImage`：保存图片中间结果，建议定期清理。

## 🤝 贡献

如果您有改进建议或发现 Bug，欢迎提交 Issue 或 Pull Request。