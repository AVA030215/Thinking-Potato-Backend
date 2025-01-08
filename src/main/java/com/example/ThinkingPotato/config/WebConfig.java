package com.example.ThinkingPotato.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOrigins("*") // 모든 출처 허용. 특정 도메인을 지정할 수도 있음.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // 허용할 HTTP 메서드
    }
}
