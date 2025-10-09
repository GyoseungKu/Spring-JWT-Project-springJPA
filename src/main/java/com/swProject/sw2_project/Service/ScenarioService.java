package com.swProject.sw2_project.Service;

import com.swProject.sw2_project.DTO.ScenarioSimpleDTO;
import com.swProject.sw2_project.Entity.Scenario;
import com.swProject.sw2_project.Repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;

    public ScenarioSimpleDTO getRandomScenario() {
        List<Scenario> scenarios = scenarioRepository.findAll();
        if (scenarios.isEmpty()) throw new IllegalStateException("시나리오가 없습니다.");
        Scenario scenario = scenarios.get(new Random().nextInt(scenarios.size()));
        return new ScenarioSimpleDTO(scenario.getScenarioId(), scenario.getSituation());
    }
}
