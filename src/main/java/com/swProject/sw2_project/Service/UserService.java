package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.Repository.CmmnUserLoginRepository;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CmmnUserLoginRepository cmmnUserLoginRepository;
    private final CmmnUserRepository cmmnUserRepository; // 추가

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
}
