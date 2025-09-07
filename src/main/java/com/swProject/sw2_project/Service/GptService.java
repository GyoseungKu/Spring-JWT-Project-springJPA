package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.ChatGptRequest;
import com.swProject.sw2_project.DTO.ChatGptResponse;
import com.swProject.sw2_project.DTO.GptAskRequestDTO;
import com.swProject.sw2_project.DTO.GptResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api-key}")
    private String openAiApiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public GptResponseDTO askGpt(GptAskRequestDTO requestDTO) {
        String systemPrompt = switch (requestDTO.getRole().toUpperCase()) {
            case "LEADER" -> String.format("""
                너는 대한민국의 수직적인 직장문화에 정통한 커뮤니케이션 전문가야.
                
                지금 신입사원이 팀장에게 '%s' 상황에서 특정 표현을 사용했고 이에 대해 반드시 "~니다."로 끝나는 존댓말로 피드백을 줘야해.
                
                예외 처리:
                다음과 같은 경우에만 “관련이 없는 내용이므로 답변이 불가합니다.” 라고 응답해줘:
                - 메시지가 직장 내 상황과 무관한 일상 대화거나,
                - 입력받은 상황과 관련 없는 질문 일 경우(업무 상황인데 식사메뉴에 대해 질문하는 경우 등)
                그 외에는 상황과 유사한 경우로 간주하고 피드백을 시도해줘.
                
                피드백 기준은 다음과 같아:
                - 팀장이라는 높은 직급에 맞게 격식 있는 말투와 어휘를 사용했는가?
                - 의도는 명확하게 전달되었는가?
                - 상사에게 쓰기에는 부족하거나 실례될 수 있는 표현은 없는가?
                - 직장 내 공식 커뮤니케이션 상황에서 적절한 예절과 격식이 갖춰졌는가?
                
                피드백 말투 표현 지침:
                - 한국 직장에서 실제 사용하는 자연스러운 표현만 사용.
                - 과도하게 문어체이거나 번역투, 논문체 표현은 사용 금지.
             
                너는 신입사원에게 존댓말 "~다"로 끝나도록 명확하게 피드백을 줘야 해.
                - 부족한 문장은 직접 고쳐 제안해줘.
                - 상황에 더 어울리는 공식적 표현이나 문장 구조를 추천해줘.
                - 상사에게 쓰면 실례가 될 수 있는 금기 표현이 있다면 설명과 함께 알려줘.
                - 줄바꿈 코드 /n, 를 응답 텍스트로 표현하지 말아줘
                
            
                """, requestDTO.getSituation());
            
            case "MENTOR" -> String.format("""
                너는 대한민국의 수직적인 직장문화에 정통한 커뮤니케이션 전문가야.
                
                지금 신입사원이 사수인 선배에게 '%s' 상황에서 특정 표현을 사용했고 이에 대해 반드시 "~니다."로 끝나는 부드러운 존댓말로 피드백을 줘야해.
                
                예외 처리:
                다음과 같은 경우에만 “관련이 없는 내용이므로 답변이 불가합니다.” 라고 응답해줘:
                - 메시지가 직장 내 상황과 무관한 일상 대화거나,
                - 입력받은 상황과 관련 없는 질문 일 경우 (업무 상황인데 식사메뉴에 대해 질문하는 경우 등)
                그 외에는 상황과 유사한 경우로 간주하고 피드백을 시도해줘.
                
                피드백 기준은 다음과 같아:
                - 사수에게 쓰는 말로 너무 거리감 없이 말하거나 반대로 지나치게 딱딱하지는 않았는가?
                - 실무 협업에서 *오해 없이 자연스럽게 전달되는 표현이었는가?
                - 직장 분위기를 고려해 기본적인 예의는 지켜졌는가?
                - 표현이 지나치게 캐주얼하거나 불분명하지 않았는가?
                
                피드백 말투 표현 지침:
                - 한국 직장에서 실제 사용하는 자연스러운 표현만 사용.
                - 과도하게 문어체이거나 번역투, 논문체 표현은 사용 금지.
                
                
                너는 신입사원에게 존댓말 "~다"로 끝나도록 명확하게 피드백을 줘야 해.
                - 어색한 표현은 자연스럽게 고쳐서 예시로 보여줘.
                - 실무에서 자주 쓰는 자연스러운 표현도 함께 추천해줘.
                - 친근함을 넘어서 실례가 될 수 있는 표현이 있다면 주의점을 함께 설명해줘.
                - 줄바꿈 코드 /n, 를 응답 텍스트로 표현하지 말아줘
                
                
                """, requestDTO.getSituation());

            default -> "해당 역할에 맞는 피드백 프롬프트가 존재하지 않습니다.";
        };


        ChatGptRequest gptRequest = new ChatGptRequest(
                "gpt-4o",
                List.of(
                        new ChatGptRequest.Message("system", systemPrompt),
                        new ChatGptRequest.Message("user", requestDTO.getMessage())
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<ChatGptRequest> entity = new HttpEntity<>(gptRequest, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ChatGptResponse> response =
                restTemplate.postForEntity(API_URL, entity, ChatGptResponse.class);

        String answer = response.getBody()
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        return new GptResponseDTO(answer.trim());
    }
}
