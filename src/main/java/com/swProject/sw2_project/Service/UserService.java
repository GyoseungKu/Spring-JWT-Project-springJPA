package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.Repository.CmmnUserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CmmnUserLoginRepository cmmnUserLoginRepository;

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
}
