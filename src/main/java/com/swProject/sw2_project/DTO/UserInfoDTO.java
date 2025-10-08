package com.swProject.sw2_project.DTO;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String userId;
    private String userNm;
    private String userEmail;
    private String regDt;
    private String chgDt;
    private String passwordChgDt;
    private Long profileImageId; // 추가
}
