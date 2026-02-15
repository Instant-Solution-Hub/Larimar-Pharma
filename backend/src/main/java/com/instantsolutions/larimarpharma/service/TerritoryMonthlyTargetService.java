package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.TerritoryMonthlyTargetResponseDto;
import com.instantsolutions.larimarpharma.DTOs.TerritoryOverviewResponseDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateTerritoryTargetDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.TerritoryMonthlyTarget;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.TerritoryMonthlyTargetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TerritoryMonthlyTargetService {

    private final FieldExecutiveRepository fieldExecutiveRepo;
    private final FieldExecutiveProfileRepository profileRepo;
    private final ManagerRepository managerRepo;
    private final TerritoryMonthlyTargetRepository territoryTargetRepo;

    @Transactional
    public List<TerritoryMonthlyTargetResponseDto> initializeTerritoryTargets(
            Long managerId,
            int month,
            int year
    ) {
        List<FieldExecutive> fes =
                fieldExecutiveRepo.findByManagerId(managerId);

        Map<String, List<FieldExecutive>> byTerritory =
                fes.stream().collect(Collectors.groupingBy(
                        FieldExecutive::getTerritory
                ));

        List<TerritoryMonthlyTarget> result = new ArrayList<>();

        for (String territory : byTerritory.keySet()) {

            Optional<TerritoryMonthlyTarget> territoryMonthlyTargetOptional =  territoryTargetRepo
                    .findByManagerIdAndTerritoryAndMonthAndYear(
                            managerId, territory, month, year
                    );
            double primarySet = profileRepo
                    .sumPrimaryTargetSet(managerId, territory, month, year);

            double secondarySet = profileRepo
                    .sumSecondaryTargetSet(managerId, territory, month, year);

            if (territoryMonthlyTargetOptional.isPresent()) {
                TerritoryMonthlyTarget territoryMonthlyTarget =  territoryMonthlyTargetOptional.get();
                territoryMonthlyTarget.setPrimaryTargetSet(primarySet);
                territoryMonthlyTarget.setSecondaryTargetSet(secondarySet);
                result.add(territoryMonthlyTarget);

                continue;
            }



            result.add(
                    TerritoryMonthlyTarget.builder()
                            .manager(managerRepo.getReferenceById(managerId))
                            .territory(territory)
                            .month(month)
                            .year(year)
                            .primaryTargetSet(primarySet)
                            .secondaryTargetSet(secondarySet)
                            .primaryTargetAchieved(0.0)
                            .secondaryTargetAchieved(0.0)
                            .subStockistStock(0)
                            .build()
            );
        }

         territoryTargetRepo.saveAll(result);

        return territoryTargetRepo
                .findByManagerIdAndMonthAndYear(managerId, month, year)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional()
    public double getTotalAchievedForManager(
            Long managerId,
            int month,
            int year
    ) {
        Double total = territoryTargetRepo
                .getTotalAchievedForManager(managerId, month, year);

        return total != null ? total : 0.0;
    }

    @Transactional
    public double getTotalAchievedForAllTerritories(
            int month,
            int year
    ) {
        Double total = territoryTargetRepo
                .getTotalAchievedForAllTerritories(month, year);

        return total != null ? total : 0.0;
    }




    @Transactional
    public TerritoryMonthlyTargetResponseDto update(
            Long territoryTargetId,
            UpdateTerritoryTargetDto dto
    ) {
        TerritoryMonthlyTarget target =
                territoryTargetRepo.findById(territoryTargetId)
                        .orElseThrow(() ->
                                new RuntimeException("Territory target not found"));

        if (dto.getPrimaryTargetAchieved() != null) {
            target.setPrimaryTargetAchieved(dto.getPrimaryTargetAchieved());
        }

        if (dto.getSecondaryTargetAchieved() != null) {
            target.setSecondaryTargetAchieved(dto.getSecondaryTargetAchieved());
        }

        if (dto.getSubStockistStock() != null) {
            target.setSubStockistStock(dto.getSubStockistStock());
        }

        territoryTargetRepo.save(target);
        return toDto(target);
    }



    public List<TerritoryOverviewResponseDto> getAllTerritoryOverview(
            int month,
            int year
    ) {

        List<TerritoryMonthlyTarget> targets =
                territoryTargetRepo.findAllWithManagerByMonthAndYear(month, year);

        return targets.stream()
                .map(target -> TerritoryOverviewResponseDto.builder()
                        .id(target.getId())
                        .territory(target.getTerritory())
                        .managerName(target.getManager().getName())

                        .primaryTarget(target.getPrimaryTargetSet())
                        .secondaryTarget(target.getSecondaryTargetSet())

                        .weeklyPrimarySale(target.getPrimaryTargetAchieved())
                        .weeklySecondarySale(target.getSecondaryTargetAchieved())

                        .totalSubstockistStock(target.getSubStockistStock())
                        .build())
                .toList();
    }


    private TerritoryMonthlyTargetResponseDto toDto(
            TerritoryMonthlyTarget t
    ) {
        return new TerritoryMonthlyTargetResponseDto(
                t.getId(),
                t.getTerritory(),
                t.getPrimaryTargetSet(),
                t.getSecondaryTargetSet(),
                t.getPrimaryTargetAchieved(),
                t.getSecondaryTargetAchieved(),
                t.getPrimaryTargetSet() - t.getPrimaryTargetAchieved(),
                t.getSecondaryTargetSet() - t.getSecondaryTargetAchieved(),
                t.getSubStockistStock()
        );
    }


}
