package com.pictech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 允许跨域的路径
//                        .allowedOrigins("*") // 允许的前端地址，如 http://localhost:5173
                        .allowedOrigins("http://192.168.3.14:8080", "http://localhost:8080") // 2. 允许来自这两个源的请求
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false) // 如果需要带 cookie，请设为 true
                        .maxAge(3600);
            }
        };
    }
}
