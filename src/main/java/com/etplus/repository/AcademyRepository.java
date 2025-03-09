package com.etplus.repository;

import com.etplus.repository.domain.AcademyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademyRepository extends JpaRepository<AcademyEntity, Long> {

  boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);
  Optional<AcademyEntity> findByRepresentativeUserId(Long representativeUserId);
}
