package com.smcompany.backend.controller;

import com.smcompany.backend.dto.request.AdminLoginRequest;
import com.smcompany.backend.entity.Admin;
import com.smcompany.backend.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request, HttpSession session) {
        Admin admin = adminService.authenticate(request.getUsername(), request.getPassword());

        // 동시 로그인 방지: 기존 세션 무효화
        adminService.registerSession(admin.getUsername(), session);

        session.setAttribute("admin", admin.getUsername());
        session.setAttribute("adminName", admin.getName());
        session.setAttribute("adminId", admin.getId());
        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "name", admin.getName()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        String username = (String) session.getAttribute("admin");
        if (username != null) {
            adminService.removeSession(username);
        }
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

    // ===== 관리자 계정 관리 =====

    @GetMapping("/list")
    public ResponseEntity<?> listAdmins(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        List<Admin> admins = adminService.getAllAdmins();
        List<Map<String, Object>> result = admins.stream().map(a -> Map.<String, Object>of(
                "id", a.getId(),
                "username", a.getUsername(),
                "name", a.getName(),
                "createdAt", a.getCreatedAt().toString()
        )).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> body, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            Admin admin = adminService.createAdmin(body.get("username"), body.get("password"), body.get("name"));
            return ResponseEntity.ok(Map.of("message", "관리자 등록 완료", "id", admin.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        Long myId = (Long) session.getAttribute("adminId");
        if (myId != null && myId.equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("message", "자기 자신은 삭제할 수 없습니다."));
        }
        adminService.deleteAdmin(id);
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        String username = (String) session.getAttribute("admin");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            adminService.changePassword(username, body.get("currentPassword"), body.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "비밀번호 변경 완료"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, HttpSession session) {
        String username = (String) session.getAttribute("admin");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        adminService.updateProfile(username, body.get("name"));
        session.setAttribute("adminName", body.get("name"));
        return ResponseEntity.ok(Map.of("message", "프로필 수정 완료", "name", body.get("name")));
    }
}
