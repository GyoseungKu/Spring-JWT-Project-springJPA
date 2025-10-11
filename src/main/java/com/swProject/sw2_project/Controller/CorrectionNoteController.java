// src/main/java/com/swProject/sw2_project/Controller/CorrectionNoteController.java
package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.CorrectionNoteResponseDTO;
import com.swProject.sw2_project.Service.CorrectionNoteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/correction-note")
public class CorrectionNoteController {

    private final CorrectionNoteService correctionNoteService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{messageId}")
    public ResponseEntity<CorrectionNoteResponseDTO> createCorrectionNote(@PathVariable Long messageId) {
        CorrectionNoteResponseDTO response = correctionNoteService.createCorrectionNote(messageId);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<CorrectionNoteResponseDTO>> getCorrectionNotesByMessageId(@PathVariable Long messageId) {
        List<CorrectionNoteResponseDTO> response = correctionNoteService.getCorrectionNotesByMessageId(messageId);
        return ResponseEntity.ok(response);
    }

}
