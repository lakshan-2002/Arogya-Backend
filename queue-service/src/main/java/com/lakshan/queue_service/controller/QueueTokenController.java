package com.lakshan.queue_service.controller;

import com.lakshan.queue_service.dto.QueueTokenCreateRequest;
import com.lakshan.queue_service.dto.QueueTokenResponse;
import com.lakshan.queue_service.dto.QueueTokenStatusUpdateRequest;
import com.lakshan.queue_service.service.QueueTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@CrossOrigin(origins = "http://localhost:5173")
public class QueueTokenController {

    private final QueueTokenService queueTokenService;

    public QueueTokenController(QueueTokenService queueTokenService) {
        this.queueTokenService = queueTokenService;
    }

    @PostMapping("/tokens")
    public ResponseEntity<QueueTokenResponse> createToken(@RequestBody QueueTokenCreateRequest request) {
        QueueTokenResponse response = queueTokenService.createToken(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/clinics/{clinicId}/tokens")
    public ResponseEntity<List<QueueTokenResponse>> getClinicQueue(@PathVariable String clinicId) {
        List<QueueTokenResponse> responses = queueTokenService.getClinicQueue(clinicId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tokens/{id}")
    public ResponseEntity<QueueTokenResponse> getToken(@PathVariable Long id) {
        QueueTokenResponse response = queueTokenService.getToken(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tokens/{id}/status")
    public ResponseEntity<QueueTokenResponse> updateStatus(@PathVariable Long id,
                                                           @RequestBody QueueTokenStatusUpdateRequest request) {
        QueueTokenResponse response = queueTokenService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
