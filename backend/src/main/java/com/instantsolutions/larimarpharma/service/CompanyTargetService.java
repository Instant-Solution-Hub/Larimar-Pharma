package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.CompanyTargetRequestDto;
import com.instantsolutions.larimarpharma.entity.CompanyTarget;
import com.instantsolutions.larimarpharma.repository.CompanyTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyTargetService {

    private final CompanyTargetRepository repository;

    public CompanyTarget addOrUpdateTarget(CompanyTargetRequestDto dto) {

        return repository.findByYearAndMonth(dto.getYear(), dto.getMonth())
                .map(existing -> {
                    existing.setTargetAmount(dto.getTargetAmount());
                    existing.setCurrentProgress(dto.getCurrentProgress());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    CompanyTarget newTarget = CompanyTarget.builder()
                            .year(dto.getYear())
                            .month(dto.getMonth())
                            .targetAmount(dto.getTargetAmount())
                            .currentProgress(dto.getCurrentProgress())
                            .build();
                    return repository.save(newTarget);
                });
    }

    public CompanyTarget getCurrentMonthTarget() {

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        return repository.findByYearAndMonth(year, month)
                .orElse(null);   // or throw exception if you prefer
    }

}

