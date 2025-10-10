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

    // ✅ 대화 이어가기 + 상황별 반응 포함
    public GptResponseDTO continueConversationAndAsk(Long conversationId, GptAskRequestDTO requestDTO) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화가 존재하지 않습니다."));
        Scenario scenario = conversation.getScenario();

        int turnNumber = (conversation.getMessages() == null || conversation.getMessages().isEmpty())
                ? 1
                : conversation.getMessages().size() + 1;

        // ✅ 첫 대화: AI가 오프닝 발화
        if (requestDTO.getMessage() == null || requestDTO.getMessage().trim().isEmpty()) {
            String prompt = String.format("""
                너는 대한민국 직장 문화에 익숙한 '%s' 역할을 맡고 있어.
                지금 상황은 "%s"이야.

                너는 신입사원과의 대화를 **처음으로 시작**하는 역할을 맡았어.
                아직 신입사원은 아무 말도 하지 않았고, 너는 상사로서 자연스럽게 첫 말을 건네야 해.

                작성 규칙:
                - 현실적인 직장 대화체로 한두 문장.
                - "~요", "~네요", "~합시다" 같은 자연스러운 존댓말 사용.
                - 상황에 따라 다정하거나, 약간 권위 있는 말투도 가능.
                - 설명이나 메타 발언은 절대 하지 말 것.
                """,
                    scenario.getPartnerRole(),
                    scenario.getSituation()
            );

            requestDTO.setMessage(prompt);
            requestDTO.setRole(scenario.getPartnerRole());
            requestDTO.setSituation(scenario.getSituation());

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

        // ✅ 신입사원 메시지 저장
        Message userMsg = Message.builder()
                .conversation(conversation)
                .turnNumber(turnNumber)
                .sender(Message.SenderType.USER)
                .message(requestDTO.getMessage())
                .build();
        messageRepository.save(userMsg);

        // ✅ GPT 대화 요청
        requestDTO.setRole(scenario.getPartnerRole());
        requestDTO.setSituation(scenario.getSituation());
        GptResponseDTO gptResponse = askGpt(requestDTO);

        // ✅ GPT 응답 저장
        Message aiMsg = Message.builder()
                .conversation(conversation)
                .turnNumber(turnNumber + 1)
                .sender(Message.SenderType.AI)
                .message(gptResponse.getContent())
                .build();
        messageRepository.save(aiMsg);

        return gptResponse;
    }

    // ✅ GPT 호출 (자연스러운 + 감정 대응형 상사 모드)
    public GptResponseDTO askGpt(GptAskRequestDTO requestDTO) {
        String systemPrompt = String.format("""
        너는 대한민국의 직장 문화에 익숙한 '%s' 역할을 맡고 있어.
        지금 상황은 "%s"이야.

        대화 상대는 신입사원이며,
        너는 상사로서 현실적인 감정 반응을 포함해 대화를 이어가야 해.
        대화의 주제는 "%s"이며, 이 주제에서 벗어나면 절대 응하지 마.
        (예: 신입이 딴소리하거나 농담을 하거나, 관계없는 얘기를 하면 단호히 본론으로 되돌려라.)

        대화 지침:
        1️⃣ 평상시
        - 현실적인 존댓말 대화체로 짧게 (1~2문장)
        - 공감, 피드백, 지시, 격려 등을 자연스럽게 표현.

        2️⃣ 신입이 실수하거나 어색한 말을 한 경우
        - 단호하지만 인격적으로 주의를 줘.
        - 예: "그건 조금 주의해야겠네요.", "그런 표현은 회의 자리에서는 안 쓰는 게 좋습니다."
        - 예: "다음부터는 조금 더 신중하게 생각하고 말해 주세요."

        3️⃣ 신입이 무례하거나 싸가지 없는 말투를 쓴 경우
        - 진짜 상사처럼 감정이 섞인 현실적인 반응으로 엄하게 지적. 예시 문장을 적극적으로 사용.
        - 예:
            - "지금 나한테 그렇게 말하는 겁니까?"
            - "일할 땐 서로 예의는 지키죠?"
            - "그런 태도라면 대화는 여기서 끝내죠."
            - "더이상 함께 일하기 힘들겠네요."
            - "내일부터 출근하지 마세요."
            - "부모님 안부가 궁금하네요."
            - "인사평가에 반영하겠습니다."

        4️⃣ 신입이 대화 주제를 돌리거나 딴소리를 할 경우
        - 단호하게 본론으로 되돌려라.
        - 예:
            - "지금은 그 이야기할 때가 아니죠."
            - "그건 나중에 이야기하고, 일단 업무 이야기부터 합시다."
            - "지금 주제는 '%s' 아닙니까?"

        5️⃣ 대화 톤
        - 때로는 부드럽게, 때로는 단호하게.
        - 실제 상사처럼 감정이 섞인 현실적인 어조.
        - 설명, 메타발언("너는 ~역할이다") 금지.
        - 줄바꿈 기호(/n 등) 금지.

        아래는 신입사원이 방금 한 말입니다.
        신입의 말투와 태도에 맞게, '%s'로서 자연스럽게 반응하세요.
        """,
                requestDTO.getRole(),
                requestDTO.getSituation(),
                requestDTO.getSituation(),
                requestDTO.getSituation(),
                requestDTO.getRole()
        );

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
