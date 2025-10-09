package com.swProject.sw2_project.Controller;

import com.swProject.sw2_project.DTO.ScenarioSimpleDTO;
import com.swProject.sw2_project.Service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scenario")
public class ScenarioController {
    private final ScenarioService scenarioService;

    @GetMapping("/random")
    public ResponseEntity<ScenarioSimpleDTO> getRandomScenario() {
        return ResponseEntity.ok(scenarioService.getRandomScenario());
    }
}
