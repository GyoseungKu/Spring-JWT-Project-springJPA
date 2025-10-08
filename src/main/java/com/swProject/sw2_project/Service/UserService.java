package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.UserInfoDTO;
import com.swProject.sw2_project.Repository.CmmnUserLoginRepository;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CmmnUserLoginRepository cmmnUserLoginRepository;
    private final CmmnUserRepository cmmnUserRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 탈퇴 (useYn, delYn 플래그 변경)
    public boolean withdrawUser(String userId) {
        try {
            var userLoginOpt = cmmnUserLoginRepository.findById(userId);
            if (userLoginOpt.isPresent()) {
                var userLogin = userLoginOpt.get();
                userLogin.setUseYn("N");
                userLogin.setDelYn("Y");
                cmmnUserLoginRepository.save(userLogin);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // 사용자 이름(닉네임) 변경
    public boolean updateUserName(String userId, String newUserName) {
        var userOpt = cmmnUserRepository.findById(userId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            user.setUserNm(newUserName); // CmmnUser의 userNm 필드
            cmmnUserRepository.save(user);
            return true;
        }
        return false;
    }

    // 사용자 정보 조회
    public Optional<UserInfoDTO> getUserInfo(String userId) {
        var userOpt = cmmnUserRepository.findById(userId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            UserInfoDTO dto = new UserInfoDTO();
            dto.setUserId(user.getUserId());
            dto.setUserNm(user.getUserNm());
            dto.setUserEmail(user.getUserEmail());
            dto.setRegDt(user.getRegDt());
            dto.setChgDt(user.getChgDt());
            if (user.getCmmnUserLogin() != null && user.getCmmnUserLogin().getChgDt() != null) {
                dto.setPasswordChgDt(user.getCmmnUserLogin().getChgDt().toString());
            }
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    // 비밀번호 확인
    public boolean checkPassword(String userId, String rawPassword) {
        var userLoginOpt = cmmnUserLoginRepository.findById(userId);
        if (userLoginOpt.isPresent()) {
            var userLogin = userLoginOpt.get();
            return passwordEncoder.matches(rawPassword, userLogin.getUserPassword());
        }
        return false;
    }
}
