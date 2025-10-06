package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.LoginRequestDTO;
import com.swProject.sw2_project.Service.LoginService;
import com.swProject.sw2_project.Util.Jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        String userId = loginRequest.getUserId();
        String password = loginRequest.getUserPassword();

        String accessToken = loginService.authenticateUser(userId, password);

        if (!"c".equals(accessToken)) {
            // 로그인 성공
            String refreshToken = jwtUtil.generateRefreshToken(userId);
            loginService.saveRefreshToken(userId, refreshToken);

            addTokenToCookie(response, "accessToken", accessToken, 15 * 60);         // 15분
            addTokenToCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60); // 7일

            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "accessToken", accessToken
                    // 필요한 경우 refreshToken은 제외하거나 분리 처리
            ));
        } else {
            // 로그인 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "아이디 또는 비밀번호가 일치하지 않습니다."
            ));
        }
    }

    private void addTokenToCookie(HttpServletResponse response, String name, String token, int maxAge) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 개발 중이면 false로 설정 가능
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
