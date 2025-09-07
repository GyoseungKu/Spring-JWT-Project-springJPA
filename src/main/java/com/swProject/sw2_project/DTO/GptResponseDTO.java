package com.swProject.sw2_project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
//GPT 응답을 프론트에 전달하기 위한 응답 DTO
public class GptResponseDTO {
    private String content; // GPT가 생성한 응답
}
