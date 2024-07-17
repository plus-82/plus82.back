package com.etplus.recruitment.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RecruitmentEntity {

    @Id
    private Long id;
    private String title;
    private String content;
}
