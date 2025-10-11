package com.swProject.sw2_project.Repository;

import com.swProject.sw2_project.Entity.CorrectionNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectionNoteRepository extends JpaRepository<CorrectionNote, Long> {

    List<CorrectionNote> findByMessage_MessageId(Long messageId);
}
