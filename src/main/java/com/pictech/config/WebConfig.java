package com.pictech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 相关配置
 * 用于添加自定义的静态资源映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 从 application.properties 中读取文件上传的根目录
     */
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // --- 核心配置：添加对 iopaint_front 文件夹的映射 ---

        // 1. 定义前端访问的 URL 模式。
        //    "/iopaint_front/**" 表示任何以 /iopaint_front/ 开头的 URL 请求都会被这个处理器拦截。
        String resourceHandler = "/iopaint_front/**";

        // 2. 定义文件在服务器上的物理存储位置。
        //    "file:" 是一个重要的前缀，告诉 Spring 这是磁盘上的一个绝对或相对路径。
        //    我们使用 uploadDir 变量和 "iopaint_front" 拼接成完整的物理路径。
        //    使用 File.separator 保证在 Windows(\) 和 Linux(/) 系统上都能正确工作。
        //    最后的 "/" 很重要，表示这是一个目录。
        String resourceLocation = "file:" + uploadDir + File.separator + "iopaint_front" + File.separator;

        // 3. 将 URL 模式和物理路径进行映射。
        registry.addResourceHandler(resourceHandler)
                .addResourceLocations(resourceLocation);

        System.out.println("============================================================");
        System.out.println("自定义静态资源映射已配置:");
        System.out.println("URL 路径: " + resourceHandler);
        System.out.println("映射到物理路径: " + resourceLocation);
        System.out.println("============================================================");
    }
}