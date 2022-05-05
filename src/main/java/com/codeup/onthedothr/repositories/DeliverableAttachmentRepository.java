package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.DeliverableAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliverableAttachmentRepository extends JpaRepository <DeliverableAttachment, Long> {
    @Query(value = "SELECT * FROM deliverable_attachments WHERE deliverable_id = ?1", nativeQuery = true)
    List<DeliverableAttachment> findDeliverableAttachmentsByDeliverableId(long deliverableId);
}
