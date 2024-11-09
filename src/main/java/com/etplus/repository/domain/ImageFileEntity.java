package com.etplus.repository.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "image_file")
public class ImageFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String path;
    private long fileSize;
    private String fileContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity owner;

    public ImageFileEntity(Long id, String fileName, String path, long fileSize, String fileContentType, UserEntity owner) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
        this.fileSize = fileSize;
        this.fileContentType = fileContentType;
        this.owner = owner;
    }
}
