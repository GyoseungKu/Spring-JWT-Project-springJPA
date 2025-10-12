package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.CmmnJoinDTO;
import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Entity.CmmnUserLogin;
import com.swProject.sw2_project.Repository.CmmnUserLoginRepository;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class JoinService {

    @Autowired
    private CmmnUserLoginRepository cmmnUserLoginRepository;

    @Autowired
    private CmmnUserRepository cmmnUserRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String chkUserId(String userId) {
        boolean exists = cmmnUserRepository.existsByUserId(userId);
        return exists ? "fail" : "success";
    }

    public Map<String, Object> registerUserLogin(CmmnJoinDTO dto) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            CmmnUser cmmnUser = new CmmnUser();
            cmmnUser.setUserId(dto.getUserId());
            cmmnUser.setUserNm(dto.getUserId());
            cmmnUser.setUserEmail(dto.getUserEmail());
            cmmnUser.setRegDt(LocalDate.now().toString());
            cmmnUser.setChgDt(LocalDate.now().toString());

            CmmnUserLogin cmmnUserLogin = new CmmnUserLogin();
            cmmnUserLogin.setUserId(dto.getUserId());
            cmmnUserLogin.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
            cmmnUserLogin.setFirPasswordYn("Y");
            cmmnUserLogin.setUseYn("Y");
            cmmnUserLogin.setDelYn("N");
            cmmnUserLogin.setRegDt(LocalDate.now());
            cmmnUserLogin.setChgDt(LocalDate.now());

            cmmnUser.setCmmnUserLogin(cmmnUserLogin);

            cmmnUserRepository.save(cmmnUser);

            rtnMap.put("status", "success");
        } catch (Exception e) {
            rtnMap.put("status", "fail");
            rtnMap.put("error", e.getMessage());
            throw e;
        }
        return rtnMap;
    }

    // 비밀번호 변경
    public String chgUserPassword(String userId, String newPassword) {
        try {
            CmmnUserLogin userLogin = cmmnUserLoginRepository.findByUserId(userId);
            if (userLogin == null) {
                return "fail";
            }
            String currentPassword = userLogin.getUserPassword();
            if (passwordEncoder.matches(newPassword, currentPassword)) {
                return "duplicate";
            }
            String beforePassword = currentPassword;
            String encodedNewPassword = passwordEncoder.encode(newPassword);

            userLogin.setBeforeUserPassword(beforePassword);
            userLogin.setUserPassword(encodedNewPassword);
            userLogin.setChgDt(LocalDate.now());

            cmmnUserLoginRepository.save(userLogin);
            return "ok";
        } catch (Exception e) {
            return "fail";
        }
    }

    // 이메일 변경
    public String changeUserEmail(String userId, String oldEmail, String newEmail) {
        CmmnUser user = cmmnUserRepository.findByUserId(userId);
        if (user == null || !user.getUserEmail().equals(oldEmail)) {
            return "fail";
        }
        user.setUserEmail(newEmail);
        user.setChgDt(LocalDate.now().toString());
        cmmnUserRepository.save(user);
        return "success";
    }
}
