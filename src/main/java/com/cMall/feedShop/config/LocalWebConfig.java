package com.cMall.feedShop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬 개발환경용 정적 파일 서빙 설정
 */
@Slf4j
@Configuration
@Profile("local")
public class LocalWebConfig implements WebMvcConfigurer {

    @Value("${file.upload.base-path:./local-uploads}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 로컬 업로드 파일 서빙
        String resourceLocation = "file:" + uploadBasePath + "/";
        
        registry.addResourceHandler("/local-uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
        
        // Mock 이미지 서빙 (MockStorageService에서 생성된 URL용)
        registry.addResourceHandler("/mock/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
        
        log.info("✅ 로컬 정적 파일 서빙 설정 완료: {} -> {}", "/local-uploads/**", resourceLocation);
    }
}