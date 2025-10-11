// src/main/java/com/swProject/sw2_project/Service/CorrectionNoteService.java
package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.CorrectionNoteResponseDTO;
import com.swProject.sw2_project.Entity.CorrectionNote;
import com.swProject.sw2_project.Entity.Message;
import com.swProject.sw2_project.Repository.CorrectionNoteRepository;
import com.swProject.sw2_project.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorrectionNoteService {

    private final MessageRepository messageRepository;
    private final CorrectionNoteRepository correctionNoteRepository;
    private final GptService gptService; // GPT 서비스 DI

    public CorrectionNoteResponseDTO createCorrectionNote(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // GPT에게 교정 요청
        String prompt = String.format("""
            아래 문장을 대한민국 직장생활에 적합한 존댓말, 예의 바른 표현으로 교정해줘.
            교정된 문장과 교정 이유를 각각 알려줘.

            원본: %s
            """, message.getMessage());

        // GPT 응답 예시: "교정된 문장: ... \n교정 이유: ..."
        String gptResult = gptService.askCorrection(prompt);

        // 결과 파싱 (예시)
        String[] parts = gptResult.split("교정 이유:");
        String correctedMsg = parts[0].replace("교정된 문장:", "").trim();
        String explanation = parts.length > 1 ? parts[1].trim() : "";

        CorrectionNote note = CorrectionNote.builder()
                .conversation(message.getConversation())
                .message(message)
                .userMessage(message.getMessage())
                .correctedMessage(correctedMsg)
                .explanation(explanation)
                .build();

        correctionNoteRepository.save(note);

        return new CorrectionNoteResponseDTO(messageId, message.getMessage(), correctedMsg, explanation);
    }

    public List<CorrectionNoteResponseDTO> getCorrectionNotesByMessageId(Long messageId) {
        List<CorrectionNote> notes = correctionNoteRepository.findByMessage_MessageId(messageId);
        return notes.stream()
                .map(note -> new CorrectionNoteResponseDTO(
                        note.getMessage().getMessageId(),
                        note.getUserMessage(),
                        note.getCorrectedMessage(),
                        note.getExplanation()
                ))
                .toList();
    }

}
