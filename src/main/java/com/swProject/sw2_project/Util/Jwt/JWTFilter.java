package com.swProject.sw2_project.Util.Jwt;

import com.swProject.sw2_project.Config.JwtAuthenticationToken;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class JWTFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JWTFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String token = null;

        // 1. 쿠키에서 accessToken 찾기
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) { // 쿠키 이름 확인
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. 토큰이 있으면 검증 후 SecurityContext에 바로 넣기
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUserId(token))) {
            String userId = jwtUtil.extractUserId(token);

            // 인증 완료된 Authentication 객체 생성 (권한은 null)
            JwtAuthenticationToken authentication =
                    new JwtAuthenticationToken(userId, token, null);
            authentication.setAuthenticated(true);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 필터 체인 계속 진행
        chain.doFilter(request, response);
    }
}
