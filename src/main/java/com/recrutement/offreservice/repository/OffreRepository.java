package com.recrutement.offreservice.repository;

import com.recrutement.offreservice.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import com.recrutement.offreservice.entity.OffreStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> ,
        JpaSpecificationExecutor<Offre> {

    List<Offre> findByRecruiterId(Long recruiterId);

    List<Offre> findByStatus(OffreStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offre o WHERE o.id = :id")
    Optional<Offre> findByIdForUpdate(@Param("id") Long id);
}