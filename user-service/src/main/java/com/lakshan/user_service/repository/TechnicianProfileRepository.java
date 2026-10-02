package com.lakshan.user_service.repository;

import com.lakshan.user_service.entity.TechnicianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnicianProfileRepository extends JpaRepository<TechnicianProfile, Integer> {
    Optional<TechnicianProfile> findByUserId(int userId);
}
