package com.swProject.sw2_project.DTO;

//  OpenAI Chat Completion API 응답 포맷 DTO

import lombok.Data;

import java.util.List;

@Data
public class ChatGptResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private ChatGptRequest.Message message;
    }
}