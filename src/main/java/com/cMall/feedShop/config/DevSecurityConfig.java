//package com.cMall.feedShop.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@Profile("dev")
//public class DevSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .requiresChannel(channel ->
//                channel.anyRequest().requiresSecure()
//            );
//        return http.build();
//    }
//}
