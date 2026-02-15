package com.smcompany.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관리자 계정 엔티티.
 * <p>시스템 관리자의 인증 정보 및 기본 정보를 관리한다.</p>
 */
@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    /** 관리자 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인에 사용되는 관리자 아이디 (고유값) */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 암호화된 비밀번호 (BCrypt) */
    @Column(nullable = false)
    private String password;

    /** 관리자 이름 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 계정 생성 일시 (최초 저장 시 자동 설정) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Admin(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
