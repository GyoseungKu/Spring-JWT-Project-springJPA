package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.ChatGptRequest;
import com.swProject.sw2_project.DTO.ChatGptResponse;
import com.swProject.sw2_project.DTO.GptAskRequestDTO;
import com.swProject.sw2_project.DTO.GptResponseDTO;
import com.swProject.sw2_project.Entity.Conversation;
import com.swProject.sw2_project.Entity.Message;
import com.swProject.sw2_project.Entity.Scenario;
import com.swProject.sw2_project.Repository.ConversationRepository;
import com.swProject.sw2_project.Repository.MessageRepository;
import com.swProject.sw2_project.Repository.ScenarioRepository;
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

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ScenarioRepository scenarioRepository;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    // 기존 대화에 메시지 추가 및 GPT 응답 저장
    public GptResponseDTO continueConversationAndAsk(Long conversationId, GptAskRequestDTO requestDTO) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화가 존재하지 않습니다."));
        Scenario scenario = conversation.getScenario();

        int turnNumber = (conversation.getMessages() == null || conversation.getMessages().isEmpty())
                ? 1
                : conversation.getMessages().size() + 1;

        // 메시지가 비어있으면 AI가 먼저 상황극을 시작
        if (requestDTO.getMessage() == null || requestDTO.getMessage().trim().isEmpty()) {
            String prompt = String.format("""
    너는 대한민국 직장문화에 익숙한 '%s' 역할을 맡고 있어.
    지금 상황은 "%s"이야.

    이 대화는 신입사원과의 역할극으로, 
    너는 상사 입장에서 **대화의 첫 문장(오프닝 발화)**을 해야 해.
    상대방(신입사원)은 아직 아무 말도 하지 않았어.  
    너만 먼저 한두 문장으로 자연스럽게 말을 꺼내.

    작성 지침:
    - 한국 직장에서 실제로 쓰는 자연스러운 말투를 사용해.
    - "~요", "~네요", "~합시다" 같은 현실적인 존댓말을 사용하되, 
      상사/사수로서의 **위계감과 자연스러운 어조를 유지**해야 해.
    - 문장은 1~2문장으로 짧고 명확하게 작성.
    - 대화 전체나 설명, '너는 ~이다' 같은 메타 문장은 포함하지 마.
    - 줄바꿈 기호(/n 등)는 사용하지 마.

    상황 예시:
    ● 보고·업무 관련:
      - "보고서 아직 정리 안 됐나요?"
      - "이번 일정 조정 관련해서 이야기 좀 할까요?"
      - "지난번 자료는 검토해봤어요?"
    ● 실수·지연 관련:
      - "지난번에 전달했던 부분은 수정됐나요?"
      - "어제 일정이 조금 밀렸던데, 이유가 있나요?"
    ● 경조사·사적 상황:
      - "결혼식 초대해줘서 고마워요. 준비는 잘 되고 있죠?"
      - "어제 회식 자리에서 이야기했던 건 좀 더 생각해봤어요?"
      - "몸이 좀 안 좋아 보이네요. 괜찮아요?"
    ● 회식·소통 상황:
      - "오늘 저녁 회식 자리 괜찮죠?"
      - "분위기 좀 풀 겸 같이 한잔합시다."
      - "요즘 팀 분위기 괜찮아요?"

    위 예시는 참고용이고, 실제 생성 시에는 
    "%s" 상황에서 '%s' 역할로서 자연스럽게 첫 발화를 만들어.
    """, scenario.getPartnerRole(), scenario.getSituation(),
                    scenario.getSituation(), scenario.getPartnerRole());
            requestDTO.setMessage(prompt);
            requestDTO.setRole(scenario.getPartnerRole());
            requestDTO.setSituation(scenario.getSituation());

            // AI 메시지로 저장
            GptResponseDTO gptResponse = askGpt(requestDTO);
            Message aiMsg = Message.builder()
                    .conversation(conversation)
                    .turnNumber(turnNumber)
                    .sender(Message.SenderType.AI)
                    .message(gptResponse.getContent())
                    .build();
            messageRepository.save(aiMsg);
            return gptResponse;
        }

        // 사용자 메시지 저장
        Message userMsg = Message.builder()
                .conversation(conversation)
                .turnNumber(turnNumber)
                .sender(Message.SenderType.USER)
                .message(requestDTO.getMessage())
                .build();
        messageRepository.save(userMsg);

        // GPT 요청 정보 세팅
        requestDTO.setRole(scenario.getPartnerRole());
        requestDTO.setSituation(scenario.getSituation());
        GptResponseDTO gptResponse = askGpt(requestDTO);

        // GPT 응답 메시지 저장
        Message aiMsg = Message.builder()
                .conversation(conversation)
                .turnNumber(turnNumber + 1)
                .sender(Message.SenderType.AI)
                .message(gptResponse.getContent())
                .build();
        messageRepository.save(aiMsg);

        return gptResponse;
    }

    // GPT API 호출
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
