package com.etplus.repository;

import com.etplus.repository.domain.ImageFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFileEntity, Long> {
}
