package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.GptAskRequestDTO;
import com.swProject.sw2_project.DTO.GptResponseDTO;
import com.swProject.sw2_project.Service.GptService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GptController {

    private final GptService gptService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/ask")
    public ResponseEntity<GptResponseDTO> askGpt(
            @RequestParam Long conversationId,
            @RequestBody GptAskRequestDTO requestDTO
    ) {
        GptResponseDTO response = gptService.continueConversationAndAsk(conversationId, requestDTO);
        return ResponseEntity.ok(response);
    }
}
