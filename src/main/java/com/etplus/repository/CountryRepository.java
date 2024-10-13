package com.etplus.repository;

import com.etplus.repository.domain.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long>,
    CountryRepositoryCustom {

}
