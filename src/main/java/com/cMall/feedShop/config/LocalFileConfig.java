package com.cMall.feedShop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 로컬 개발환경용 파일 업로드 디렉토리 설정
 */
@Slf4j
@Configuration
@Profile({"local", "test"})
public class LocalFileConfig {

    @Value("${file.upload.base-path:./local-uploads}")
    private String uploadBasePath;

    @PostConstruct
    public void createUploadDirectories() {
        try {
            // 기본 업로드 디렉토리 생성
            createDirectoryIfNotExists(uploadBasePath);
            
            // 각 업로드 타입별 디렉토리 생성
            createDirectoryIfNotExists(uploadBasePath + "/images");
            createDirectoryIfNotExists(uploadBasePath + "/images/products");
            createDirectoryIfNotExists(uploadBasePath + "/images/feeds");
            createDirectoryIfNotExists(uploadBasePath + "/images/reviews");
            createDirectoryIfNotExists(uploadBasePath + "/images/profiles");
            createDirectoryIfNotExists(uploadBasePath + "/temp");
            
            log.info("✅ 로컬 파일 업로드 디렉토리 생성 완료: {}", uploadBasePath);
            
        } catch (Exception e) {
            log.error("❌ 로컬 파일 업로드 디렉토리 생성 실패", e);
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.debug("디렉토리 생성: {}", directoryPath);
            }
        } catch (Exception e) {
            log.error("디렉토리 생성 실패: {}", directoryPath, e);
        }
    }
}