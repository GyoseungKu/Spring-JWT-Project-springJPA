package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdrawUser(java.security.Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "인증 정보가 없습니다."));
        }
        String userId = principal.getName();
        boolean result = userService.withdrawUser(userId);
        if (result) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } else {
            return ResponseEntity.status(400).body(java.util.Map.of("message", "회원 탈퇴에 실패했습니다."));
        }
    }
}
