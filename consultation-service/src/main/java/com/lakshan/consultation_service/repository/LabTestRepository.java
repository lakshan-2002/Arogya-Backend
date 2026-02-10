package com.lakshan.consultation_service.repository;

import com.lakshan.consultation_service.domain.LabTest;
import com.lakshan.consultation_service.domain.LabTest.TestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    
    List<LabTest> findByConsultationId(Long consultationId);
    
    Page<LabTest> findByStatus(TestStatus status, Pageable pageable);
    
    Page<LabTest> findByAssignedTechnicianId(Long technicianId, Pageable pageable);
    
    @Query("SELECT lt FROM LabTest lt WHERE lt.status = :status AND lt.assignedTechnicianId = :technicianId")
    Page<LabTest> findByStatusAndTechnicianId(@Param("status") TestStatus status, 
                                               @Param("technicianId") Long technicianId, 
                                               Pageable pageable);
    
    @Query("SELECT lt FROM LabTest lt WHERE lt.createdAt BETWEEN :start AND :end")
    Page<LabTest> findByCreatedAtBetween(@Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end, 
                                          Pageable pageable);
}
