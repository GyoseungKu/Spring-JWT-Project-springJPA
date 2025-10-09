package com.swProject.sw2_project.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GptAskRequestDTO {
    private String message;

    @Schema(hidden = true)
    private String role;

    @Schema(hidden = true)
    private String situation;
}
