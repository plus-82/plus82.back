package com.etplus.recruitment.application;

import com.etplus.recruitment.application.port.in.RecruitmentCommandUseCase;
import com.etplus.recruitment.application.port.in.RecruitmentQueryUseCase;
import com.etplus.recruitment.application.port.out.RecruitmentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecruitmentService implements RecruitmentCommandUseCase, RecruitmentQueryUseCase {

    private final RecruitmentStorage recruitmentRepository;

    @Override
    public long createRecruitment() {
        return 0;
    }
}
