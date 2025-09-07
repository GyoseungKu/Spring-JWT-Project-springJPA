package com.swProject.sw2_project.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/hello")
    public ResponseEntity<String> testHello() {
        return ResponseEntity.ok("로그인 시 이용가능");
    }
}
