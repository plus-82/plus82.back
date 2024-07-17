package com.etplus.recruitment.adapter.client;

import com.etplus.recruitment.application.port.in.RecruitmentCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recruitments")
public class RecruitmentController {

    private final RecruitmentCommandUseCase recruitmentCommandUseCase;

    @PostMapping
    public ResponseEntity<Long> createRecruitment() {
        return ResponseEntity.ok(recruitmentCommandUseCase.createRecruitment());
    }

}
