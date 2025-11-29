package com.lakshan.clinic_service.repository;

import com.lakshan.clinic_service.entity.ClinicDoctors;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicDoctorsRepository extends JpaRepository<ClinicDoctors, Integer> {

    @Modifying
    @Query("DELETE FROM ClinicDoctors cd WHERE cd.clinic.id = :clinicId ")
    void deleteByClinicId(@Param("clinicId") int clinicId);

    List<ClinicDoctors> findByClinicId(int clinicId);
}
