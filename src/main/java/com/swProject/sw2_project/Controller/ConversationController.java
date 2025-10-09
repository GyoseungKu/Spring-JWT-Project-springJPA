package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.MessageSimpleDTO;
import com.swProject.sw2_project.Entity.Message;
import com.swProject.sw2_project.Service.ConversationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/start")
    public ResponseEntity<Long> startConversation(
            @RequestParam Long scenarioId,
            @AuthenticationPrincipal String userId
    ) {
        System.out.println("userId from token: " + userId);
        Long conversationId = conversationService.createConversation(scenarioId, userId);
        return ResponseEntity.ok(conversationId);
    }

    @GetMapping("/{conversationId}/messages")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<MessageSimpleDTO>> getConversationMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal String userId
    ) {
        List<Message> messages = conversationService.getMessagesIfOwner(conversationId, userId);
        List<MessageSimpleDTO> dtos = messages.stream()
                .map(m -> new MessageSimpleDTO(
                        m.getMessageId(),
                        m.getTurnNumber(),
                        m.getSender().name(),
                        m.getMessage(),
                        m.getCreatedAt().toString()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}