package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Config.JwtAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/test/hello")
    public ResponseEntity<String> testHello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken && authentication.isAuthenticated()) {
            return ResponseEntity.ok("로그인 상태입니다");
        } else {
            return ResponseEntity.status(401).body("로그인 필요합니다");
        }
    }
}
