package com.smcompany.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello SSAFIT World! 배포 환경 테스트 중입니다.";
    }
}