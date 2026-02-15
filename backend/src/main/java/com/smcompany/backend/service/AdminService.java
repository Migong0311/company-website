package com.smcompany.backend.service;

import com.smcompany.backend.entity.Admin;
import com.smcompany.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

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
}
