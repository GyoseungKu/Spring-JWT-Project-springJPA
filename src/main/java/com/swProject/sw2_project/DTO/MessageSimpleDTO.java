package com.swProject.sw2_project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageSimpleDTO {
    private Long messageId;
    private Integer turnNumber;
    private String sender;
    private String message;
    private String createdAt;
}
