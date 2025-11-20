package com.lakshan.user_service.repository;

import com.lakshan.user_service.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Integer> {
    Optional<AdminProfile> findByUserId(int userId);
}
