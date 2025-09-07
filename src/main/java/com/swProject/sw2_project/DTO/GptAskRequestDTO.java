package com.swProject.sw2_project.DTO;

import lombok.Data;
// 프론트에서 넘겨주는 질문 정보
@Data
public class GptAskRequestDTO {
    private String role;    // 사수, 팀장급 구분
    private String message; // 질문 내용
    private  String situation;
}
