package com.swProject.sw2_project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//OpenAI Chat Completion API 요청 포맷 DTO (프론트가 아닌 OpenAi와 소통용)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptRequest {
    private String model = "gpt-4o";
    private List<Message> messages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;     // system or user
        private String content;  // 메시지 내용
    }
}
