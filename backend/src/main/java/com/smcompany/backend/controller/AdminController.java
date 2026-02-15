package com.smcompany.backend.controller;

import com.smcompany.backend.dto.request.AdminLoginRequest;
import com.smcompany.backend.entity.Admin;
import com.smcompany.backend.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request, HttpSession session) {
        Admin admin = adminService.authenticate(request.getUsername(), request.getPassword());
        session.setAttribute("admin", admin.getUsername());
        session.setAttribute("adminName", admin.getName());
        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "name", admin.getName()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(HttpSession session) {
        String admin = (String) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(Map.of(
                    "loggedIn", true,
                    "name", session.getAttribute("adminName")
            ));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }
}
