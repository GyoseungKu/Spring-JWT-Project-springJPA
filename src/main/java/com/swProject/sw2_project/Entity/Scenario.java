package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "scenario")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scenarioId;

    @Column(nullable = false)
    private String partnerRole; // 상사, 동료 등

    @Column(nullable = false)
    private String situation; // 대화 상황

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tutorPrompt; // AI 튜터 프롬프트

    @Column(columnDefinition = "TEXT")
    private String notePrompt; // 오답노트 프롬프트

    private Integer difficulty; // 난이도 1~5

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Conversation> conversations;
}

