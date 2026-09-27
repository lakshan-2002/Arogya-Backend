package com.lakshan.user_service.repository;

import com.lakshan.user_service.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Integer> {
    DoctorProfile findByUserId(int userId);

    Optional<DoctorProfile> findByIdAndUserId(int id, int userId);
}
