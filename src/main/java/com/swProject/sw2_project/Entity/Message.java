package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(nullable = false)
    private Integer turnNumber; // 대화 순서

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType sender; // USER, AI

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum SenderType {
        USER,
        AI
    }
}
