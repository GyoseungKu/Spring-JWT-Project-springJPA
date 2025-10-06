package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.CmmnJoinDTO;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import com.swProject.sw2_project.Service.EmailAuthService;
import com.swProject.sw2_project.Service.JoinService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/join")
@Slf4j
public class JoinController {

    @Autowired
    private JoinService joinService;

    @Autowired
    private EmailAuthService emailAuthService;

    @Autowired
    private CmmnUserRepository cmmnUserRepository;

    // 회원가입 처리
    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@RequestBody CmmnJoinDTO cmmnJoinDTO, @RequestParam int authCode) {
        String chkEmailAuth = emailAuthService.validateAuthCode(cmmnJoinDTO.getUserEmail(), authCode);

        if ("Y".equals(chkEmailAuth)) {
            Map<String, Object> rtnMap = joinService.registerUserLogin(cmmnJoinDTO);
            String status = (String) rtnMap.get("status");
            if ("success".equals(status)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "회원가입 실패"));
    }

    // 아이디 중복 확인
    @GetMapping("/chkUserId")
    public ResponseEntity<?> chkUserId(@RequestParam String userId) {
        String chkUserId = joinService.chkUserId(userId);
        if ("success".equals(chkUserId)) {
            return ResponseEntity.ok(Map.of("message", "사용 가능한 아이디입니다."));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "사용 불가능한 아이디입니다."));
        }
    }
    // 인증 이메일 전송
    @PostMapping("/sendChangeEmail")
    public ResponseEntity<?> sendChangeEmail(@RequestParam String userEmail,@RequestParam String userId) {
        String auth = null;
        try {
            String type = "change";
            if (userId != null && !userId.equals("")) {
                auth = emailAuthService.sendEmail(userEmail, type);
                if (auth.equals("y")) {
                    return ResponseEntity.ok(Map.of("message", "이메일을 성공적으로 보냈습니다."));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "해당 이메일로 가입한 계정이 존재하지 않습니다. "));
                }
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "해당 이메일로 가입한 계정이 존재하지 않습니다. "));
            }
        } catch(Exception e){
            log.error("이메일 전송 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이메일 전송에 실패했습니다."));
        }
    }

    // 비밀번호 찾기 이메일 인증 코드 확인
    @PostMapping("/authPasswordEmail")
    public ResponseEntity<?> authPasswordEmail(@RequestParam String userId,@RequestParam String userEmail, @RequestParam int authCode) {
        try {
            String result = emailAuthService.validateAuthCode(userEmail, authCode);
            if ("Y".equals(result)) {
                String chk = cmmnUserRepository.findByChkUserId(userId);
                return ResponseEntity.ok(Map.of("message", "이메일 인증 성공"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "이메일 인증 실패"));
            }
        } catch (Exception e) {
            log.error("이메일 인증 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이메일 인증 처리에 실패했습니다."));
        }
    }

    // 인증 이메일 전송
    @PostMapping("/sendAuthEmail")

    public ResponseEntity<?> sendAuthEmail(@RequestParam String userEmail,@RequestParam String type) {
        try {
            String id = null;
            String auth = null;
            id = cmmnUserRepository.findUserIdByEmail(userEmail);

            if(type.equals("find")){
                //이메일 존재여부 확인
                if(id != null && !id.equals("")){
                    auth = emailAuthService.sendEmail(userEmail,type);
                }else{
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "해당 이메일로 가입한 계정이 존재하지 않습니다. "));
                }
            }
            else if(id == null || id.equals("")){
                auth = emailAuthService.sendEmail(userEmail,type);
            }
            if(auth.equals("y")){
                return ResponseEntity.ok(Map.of("message", "이메일을 성공적으로 보냈습니다."));
            }
            else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이메일 전송에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("이메일 전송 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이메일 전송에 실패했습니다."));
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/authEmail")
    public ResponseEntity<?> authEmail(@RequestParam String userEmail, @RequestParam int authCode , @RequestParam String type) {
        try {
            String result = emailAuthService.validateAuthCode(userEmail, authCode);
            if ("Y".equals(result)) {
                if(type.equals("find")){
                    String id = cmmnUserRepository.findUserIdByEmail(userEmail);
                    return ResponseEntity.ok(Map.of("id",id));
                }
                else{
                    return ResponseEntity.ok(Map.of("message", "이메일 인증 성공"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "이메일 인증 실패"));
            }
        } catch (Exception e) {
            log.error("이메일 인증 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이메일 인증 처리에 실패했습니다."));
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/chgUserPassword")
    public ResponseEntity<?> chgUserPassword(@RequestParam String userEmail,
                                             @RequestParam int authCode,
                                             @RequestParam String password,
                                             @RequestParam String userId) {
        try {
            String result = emailAuthService.validateAuthCode(userEmail, authCode);
            if ("Y".equals(result)) {
                String chgUserPassword = joinService.chgUserPassword(userId, password);
                if ("duplicate".equals(chgUserPassword)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("message", "이전 비밀번호로 변경할 수 없습니다."));
                } else if ("ok".equals(chgUserPassword)) {
                    return ResponseEntity.ok(Map.of("message", "비밀번호 변경 성공"));
                } else if ("fail".equals(chgUserPassword)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("message", "비밀번호 변경 실패. 관리자에게 문의해주세요."));
                } else {
                    // 비정상 반환 처리
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "알 수 없는 오류 발생"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "이메일 인증 및 아이디 조회 실패"));
            }
        } catch (Exception e) {
            log.error("이메일 인증 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "이메일 인증 처리에 실패했습니다."));
        }
    }

    // 아이디 찾기
    @PostMapping("/findUserId")
    public ResponseEntity<?> findUserId(@RequestParam String userEmail, @RequestParam int authCode) {
        try {
            String result = emailAuthService.validateAuthCode(userEmail, authCode);
            if ("Y".equals(result)) {
                String userId = cmmnUserRepository.findUserIdByEmail(userEmail);
                if (userId != null && !userId.isEmpty()) {
                    return ResponseEntity.ok(Map.of("userId", userId, "userEmail", userEmail));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "해당 이메일로 가입된 아이디가 없습니다."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "이메일 인증 실패"));
            }
        } catch (Exception e) {
            log.error("아이디 찾기 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "아이디 찾기 처리에 실패했습니다."));
        }
    }

}
