package com.etplus.repository;

import com.etplus.repository.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByIdIn(List<Long> imageFileIdList);
}
