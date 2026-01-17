package com.lakshan.queue_service.repository;

import com.lakshan.queue_service.entity.QueueToken;
import com.lakshan.queue_service.entity.QueueTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    List<QueueToken> findByClinicIdAndStatusOrderByPositionAsc(String clinicId, QueueTokenStatus status);

    Optional<QueueToken> findTopByClinicIdOrderByTokenNumberDesc(String clinicId);

    List<QueueToken> findByPatientIdAndStatusInOrderByIssuedAtAsc(String patientId, List<QueueTokenStatus> statuses);
}
