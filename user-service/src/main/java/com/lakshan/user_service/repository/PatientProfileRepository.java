package com.lakshan.user_service.repository;

import com.lakshan.user_service.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Integer> {
    PatientProfile findByUserId(int userId);

    Optional<PatientProfile> findByIdAndUserId(int id, int userId);
}
