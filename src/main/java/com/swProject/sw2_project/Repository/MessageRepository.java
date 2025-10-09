package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
