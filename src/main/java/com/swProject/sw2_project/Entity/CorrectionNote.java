package com.swProject.sw2_project.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "correction_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectionNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String correctedMessage;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
