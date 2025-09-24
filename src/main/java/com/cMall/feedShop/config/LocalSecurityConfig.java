package com.cMall.feedShop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 로컬 개발환경 전용 Security 설정
 * - 모든 API 인증 없이 접근 가능
 * - 캐시 테스트 및 기능 개발에 최적화
 */
@Slf4j
@Configuration
@EnableWebSecurity
@Profile("local")
public class LocalSecurityConfig {

    @Bean
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔓 로컬 개발환경: 모든 API 인증 없이 접근 가능");
        
        http
                // CSRF 보호 비활성화 (로컬 테스트 편의성)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 모든 요청에 대해 인증 없이 접근 허용
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                
                // 폼 로그인 및 HTTP Basic 인증 비활성화
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 로컬 개발환경에서 허용할 Origin 목록
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",    // React 개발 서버
                "http://localhost:8080",    // Spring Boot 서버
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8080"
        ));
        
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        config.setAllowCredentials(true); // 자격 증명 허용
        config.setMaxAge(3600L); // 1시간 캐시
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
}