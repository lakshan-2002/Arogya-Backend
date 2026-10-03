package com.lakshan.medical_records_service.repository;

import com.lakshan.medical_records_service.domain.TestResultFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestResultFileRepository extends JpaRepository<TestResultFile, Long> {
    Optional<TestResultFile> findByIdAndTestResultId(Long id, Long testResultId);
}
