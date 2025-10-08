package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scenario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scenarioId;

    @Column(nullable = false, length = 50)
    private String partnerRole; // 상사, 동료 등

    @Column(nullable = false, length = 255)
    private String situation; // 대화 상황 요약

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tutorPrompt; // AI 튜터용 프롬프트

    @Column(columnDefinition = "TEXT")
    private String notePrompt; // 오답노트용 프롬프트

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Conversation> conversations;
}

