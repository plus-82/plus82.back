package com.etplus.repository;

import com.etplus.repository.domain.ImageFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFileEntity, Long> {

    List<ImageFileEntity> findAllByIdIn(List<Long> imageFileIdList);
}
