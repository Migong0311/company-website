package com.smcompany.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 자료실: 누구나 조회/다운로드 가능
                        .requestMatchers(HttpMethod.GET, "/api/references/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reference-categories/**").permitAll()

                        // Q&A: 누구나 조회/작성 가능
                        .requestMatchers(HttpMethod.GET, "/api/qna/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/qna").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/qna/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/qna/*/check-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/qna/comments/*/check-password").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/qna/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/qna/**").permitAll()

                        // 관리자 로그인/체크
                        .requestMatchers("/api/admin/**").permitAll()

                        // 관리자 전용: 자료실 등록/삭제, 카테고리 관리
                        .requestMatchers(HttpMethod.POST, "/api/references/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/references/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/references/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reference-categories/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/reference-categories/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/reference-categories/**").permitAll()

                        // 나머지
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
