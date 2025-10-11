package com.swProject.sw2_project.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrectionNoteResponseDTO {
    private Long messageId;
    private String userMessage;
    private String correctedMessage;
    private String explanation;
}
