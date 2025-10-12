package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.CheckPasswordDTO;
import com.swProject.sw2_project.DTO.UpdateUserNameDTO;
import com.swProject.sw2_project.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원 탈퇴
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

    // 사용자 이름(닉네임) 변경
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/name")
    public ResponseEntity<?> updateUserName(
            java.security.Principal principal,
            @RequestBody UpdateUserNameDTO request) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증 정보가 없습니다."));
        }
        String userId = principal.getName();
        boolean result = userService.updateUserName(userId, request.getUserName());
        if (result) {
            return ResponseEntity.ok(Map.of("message", "사용자 이름이 변경되었습니다."));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "사용자 이름 변경에 실패했습니다."));
        }
    }

    // 사용자 정보 조회
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<?> getMyUserInfo(java.security.Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증 정보가 없습니다."));
        }
        String userId = principal.getName();
        var userOpt = userService.getUserInfo(userId);
        if (userOpt.isPresent()) {
            var dto = userOpt.get();
            var userEntityOpt = userService.findById(userId);
            if (userEntityOpt.isPresent() && userEntityOpt.get().getProfileImage() != null) {
                dto.setProfileImageId(userEntityOpt.get().getProfileImage().getImageId());
            }
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }
    }

    // 비밀번호 확인
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/password")
    public ResponseEntity<?> checkPassword(
            java.security.Principal principal,
            @RequestBody CheckPasswordDTO request) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "인증 정보가 없습니다."));
        }
        String userId = principal.getName();
        boolean match = userService.checkPassword(userId, request.getPassword());
        if (match) {
            return ResponseEntity.ok(java.util.Map.of("match", true));
        } else {
            return ResponseEntity.status(400).body(java.util.Map.of("match", false, "message", "비밀번호가 일치하지 않습니다."));
        }
    }

    // 비밀번호 변경 (마이페이지)
    @PostMapping("/password/change")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> changePassword(
            java.security.Principal principal,
            @RequestBody com.swProject.sw2_project.DTO.ChangePasswordDTO request) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증 정보가 없습니다."));
        }
        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "비밀번호를 입력해주세요."));
        }
        String userId = principal.getName();
        boolean result = userService.changePassword(userId, newPassword);
        if (result) {
            return ResponseEntity.ok(Map.of("message", "비밀번호 변경 성공"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "비밀번호 변경 실패"));
        }
    }

}
