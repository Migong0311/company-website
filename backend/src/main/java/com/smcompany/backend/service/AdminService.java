package com.smcompany.backend.service;

import com.smcompany.backend.entity.Admin;
import com.smcompany.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /** username → 현재 활성 세션 (동시 로그인 방지) */
    private final ConcurrentHashMap<String, HttpSession> activeSessions = new ConcurrentHashMap<>();

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    public Admin authenticate(String username, String password) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return admin;
    }

    public void registerSession(String username, HttpSession newSession) {
        HttpSession oldSession = activeSessions.put(username, newSession);
        if (oldSession != null && !oldSession.getId().equals(newSession.getId())) {
            try {
                oldSession.invalidate();
            } catch (IllegalStateException ignored) {
                // 이미 만료된 세션
            }
        }
    }

    public void removeSession(String username) {
        activeSessions.remove(username);
    }

    public void initAdmin() {
        if (!adminRepository.existsByUsername(adminUsername)) {
            Admin admin = Admin.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .name(adminName)
                    .build();
            adminRepository.save(admin);
        }
    }

    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Transactional
    public Admin createAdmin(String username, String password, String name) {
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        Admin admin = Admin.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
        return adminRepository.save(admin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        admin.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateProfile(String username, String name) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
        admin.updateProfile(name);
    }
}
