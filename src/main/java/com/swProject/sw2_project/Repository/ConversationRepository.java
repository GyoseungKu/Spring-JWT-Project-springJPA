package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
