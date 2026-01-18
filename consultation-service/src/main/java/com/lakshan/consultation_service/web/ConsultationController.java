package com.lakshan.consultation_service.web;

import com.lakshan.consultation_service.domain.Consultation.Status;
import com.lakshan.consultation_service.dto.ConsultationDtos;
import com.lakshan.consultation_service.service.ConsultationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/consultations")
@Validated
public class ConsultationController {

    private final ConsultationService service;

    public ConsultationController(ConsultationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsultationDtos.Response> create(@RequestBody @Validated ConsultationDtos.CreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDtos.Response> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<ConsultationDtos.Response>> list(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long clinicId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.list(patientId, doctorId, clinicId, status, start, end, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDtos.Response> update(@PathVariable Long id, @RequestBody ConsultationDtos.UpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ConsultationDtos.Response> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ConsultationDtos.Response> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.complete(id));
    }
}
