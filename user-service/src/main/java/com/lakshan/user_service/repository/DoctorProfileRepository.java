package com.lakshan.user_service.repository;

import com.lakshan.user_service.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Integer> {
    Optional<DoctorProfile> findByUserId(int userId);
}
