package com.lakshan.medical_records_service.repository;

import com.lakshan.medical_records_service.domain.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResult> findByLabTestId(Long labTestId);
    List<TestResult> findByPatientId(Long patientId);
    Page<TestResult> findByPatientId(Long patientId, Pageable pageable);
    List<TestResult> findByTechnicianId(Long technicianId);
    boolean existsByLabTestId(Long labTestId);
}
