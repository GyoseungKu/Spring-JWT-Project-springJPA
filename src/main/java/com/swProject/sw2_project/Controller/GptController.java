package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.GptAskRequestDTO;
import com.swProject.sw2_project.DTO.GptResponseDTO;
import com.swProject.sw2_project.Service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GptController {

    private final GptService gptService;

    @PostMapping("/ask")
    public ResponseEntity<GptResponseDTO> askGpt(@RequestBody GptAskRequestDTO requestDTO) {
        GptResponseDTO response = gptService.askGpt(requestDTO);
        return ResponseEntity.ok(response);
    }
}