package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.Entity.Conversation;
import com.swProject.sw2_project.Entity.Message;
import com.swProject.sw2_project.Entity.Scenario;
import com.swProject.sw2_project.Entity.CmmnUser;
import com.swProject.sw2_project.Repository.ConversationRepository;
import com.swProject.sw2_project.Repository.ScenarioRepository;
import com.swProject.sw2_project.Repository.CmmnUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ScenarioRepository scenarioRepository;
    private final CmmnUserRepository cmmnUserRepository;

    public Long createConversation(Long scenarioId, String userId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("시나리오 없음"));
        CmmnUser user = cmmnUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Conversation conversation = Conversation.builder()
                .scenario(scenario)
                .user(user)
                .build();
        conversationRepository.save(conversation);
        return conversation.getConversationId();
    }

    public List<Message> getMessagesIfOwner(Long conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화 없음"));
        if (!conversation.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인만 조회 가능합니다.");
        }
        return conversation.getMessages();
    }
}
