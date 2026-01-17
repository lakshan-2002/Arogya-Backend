package com.lakshan.queue_service.service;

import com.lakshan.queue_service.client.ClinicServiceClient;
import com.lakshan.queue_service.client.UserServiceClient;
import com.lakshan.queue_service.dto.QueueTokenCreateRequest;
import com.lakshan.queue_service.dto.QueueTokenResponse;
import com.lakshan.queue_service.dto.QueueTokenStatusUpdateRequest;
import com.lakshan.queue_service.entity.QueueToken;
import com.lakshan.queue_service.entity.QueueTokenStatus;
import com.lakshan.queue_service.repository.QueueTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueueTokenService {

    private final QueueTokenRepository queueTokenRepository;
    private final UserServiceClient userServiceClient;
    private final ClinicServiceClient clinicServiceClient;

    public QueueTokenService(QueueTokenRepository queueTokenRepository,
                             UserServiceClient userServiceClient,
                             ClinicServiceClient clinicServiceClient) {
        this.queueTokenRepository = queueTokenRepository;
        this.userServiceClient = userServiceClient;
        this.clinicServiceClient = clinicServiceClient;
    }

    @Transactional
    public QueueTokenResponse createToken(QueueTokenCreateRequest request) {
        String clinicId = request.getClinicId();
        String patientId = request.getPatientId();

        // Validate clinicId and patientId via other microservices
        int clinicIntId = Integer.parseInt(clinicId);
        int patientIntId = Integer.parseInt(patientId);

        // These calls will throw if not found, which will bubble up as 5xx unless you add a controller advice
        clinicServiceClient.getClinicById(clinicIntId);
        userServiceClient.getUserById(patientIntId);

        Optional<QueueToken> lastTokenOpt = queueTokenRepository.findTopByClinicIdOrderByTokenNumberDesc(clinicId);
        int nextTokenNumber = lastTokenOpt.map(t -> t.getTokenNumber() + 1).orElse(1);

        List<QueueToken> activeTokens = queueTokenRepository
                .findByClinicIdAndStatusOrderByPositionAsc(clinicId, QueueTokenStatus.PENDING);
        int nextPosition = activeTokens.size() + 1;

        QueueToken token = new QueueToken();
        token.setTokenNumber(nextTokenNumber);
        token.setClinicId(clinicId);
        token.setPatientId(patientId);
        token.setConsultationId(request.getConsultationId());
        token.setStatus(QueueTokenStatus.PENDING);
        token.setPosition(nextPosition);
        token.setIssuedAt(LocalDateTime.now());
        token.setUpdatedAt(LocalDateTime.now());

        QueueToken saved = queueTokenRepository.save(token);
        return toResponse(saved);
    }

    public List<QueueTokenResponse> getClinicQueue(String clinicId) {
        List<QueueToken> tokens = queueTokenRepository
                .findByClinicIdAndStatusOrderByPositionAsc(clinicId, QueueTokenStatus.PENDING);
        return tokens.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public QueueTokenResponse getToken(Long id) {
        QueueToken token = queueTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Token not found with id: " + id));
        return toResponse(token);
    }

    @Transactional
    public QueueTokenResponse updateStatus(Long id, QueueTokenStatusUpdateRequest request) {
        QueueToken token = queueTokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Token not found with id: " + id));

        token.setStatus(request.getStatus());
        token.setUpdatedAt(LocalDateTime.now());

        QueueToken saved = queueTokenRepository.save(token);
        return toResponse(saved);
    }

    private QueueTokenResponse toResponse(QueueToken token) {
        QueueTokenResponse response = new QueueTokenResponse();
        response.setId(token.getId());
        response.setTokenNumber(token.getTokenNumber());
        response.setClinicId(token.getClinicId());
        response.setPatientId(token.getPatientId());
        response.setConsultationId(token.getConsultationId());
        response.setStatus(token.getStatus());
        response.setPosition(token.getPosition());
        response.setIssuedAt(token.getIssuedAt());
        response.setUpdatedAt(token.getUpdatedAt());
        return response;
    }
}
