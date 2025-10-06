package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.CmmnUserLoginTokenDTO;
import com.swProject.sw2_project.Entity.CmmnUserLogin;
import com.swProject.sw2_project.Entity.CmmnUserLoginToken;
import com.swProject.sw2_project.Entity.UserLoginTokenId;
import com.swProject.sw2_project.Repository.CmmnUserLoginRepository;
import com.swProject.sw2_project.Repository.CmmnUserLoginTokenRepository;
import com.swProject.sw2_project.Util.Jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private CmmnUserLoginRepository cmmnUserLoginRepository;

    @Autowired
    private CmmnUserLoginTokenRepository cmmnUserLoginTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 로그인 인증 및 JWT 토큰 발급
    public String authenticateUser(String userId, String password) {
        CmmnUserLogin userLogin = cmmnUserLoginRepository.findByUserId(userId);

        if (userLogin != null) {
            if (!"Y".equals(userLogin.getUseYn()) || !"N".equals(userLogin.getDelYn())) {
                throw new RuntimeException("탈퇴한 사용자입니다");
            }
            if (passwordEncoder.matches(password, userLogin.getUserPassword())) {
                String accessToken = jwtUtil.generateAccessToken(userId);
                String refreshToken = jwtUtil.generateRefreshToken(userId);

                saveRefreshToken(userId, refreshToken);
                Date tokenExpiration = jwtUtil.extractClaims(accessToken).getExpiration();
                CmmnUserLoginTokenDTO loginTokenDTO = new CmmnUserLoginTokenDTO(userId, refreshToken, tokenExpiration);

                saveUserLoginToken(loginTokenDTO);
                return accessToken;
            }
        }
        // 인증 실패 시
        return "c";
    }

    public void saveRefreshToken(String userId, String refreshToken) {
        Date exp = jwtUtil.extractClaims(refreshToken).getExpiration();
        CmmnUserLoginTokenDTO loginTokenDTO = new CmmnUserLoginTokenDTO(userId, refreshToken, exp);
        saveUserLoginToken(loginTokenDTO);
    }

    @Transactional
    public void saveUserLoginToken(CmmnUserLoginTokenDTO dto) {
        log.info("dto : " + dto);
        CmmnUserLogin user = cmmnUserLoginRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Optional<CmmnUserLoginToken> existingToken = cmmnUserLoginTokenRepository
                .findByChkUserId(dto.getUserId());

        CmmnUserLoginToken entity;

        if (existingToken.isPresent()) {
            entity = existingToken.get();
            entity.setTokenExpDt(dto.getTokenExpiration());
        } else {
            UserLoginTokenId tokenId = new UserLoginTokenId(dto.getRefreshToken(), dto.getUserId());
            entity = new CmmnUserLoginToken(tokenId, dto.getTokenExpiration());
        }

        entity.setCmmnUserLogin(user);
        cmmnUserLoginTokenRepository.save(entity);
    }
}
