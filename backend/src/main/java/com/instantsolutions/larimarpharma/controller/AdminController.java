package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/contact")
    public ResponseEntity<ApiResponseDto<AdminContactResponseDto>> getAdminContact() {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAdminContact(),
                        "Contact details fetched successfully"
                )
        );
    }

    @GetMapping("/{adminId}/dashboard-stats")
    public ResponseEntity<ApiResponseDto<AdminDashboardStatsDto>> getAdminDashboardStats(
            @PathVariable Long adminId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAdminDashboardStats(adminId),
                        "Admin dashboard statistics fetched successfully"
                )
        );
    }

    @PutMapping("/{adminId}/contact/basic")
    public ResponseEntity<ApiResponseDto<AdminContactResponseDto>> updateContactDetails(
            @PathVariable Long adminId,
            @RequestBody ManagerContactUpdateRequestDto dto
    ) {
        AdminContactResponseDto updated =
                adminService.updateContactDetails(adminId, dto);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        updated,
                        "Contact details updated successfully"
                )
        );
    }

//    @GetMapping("/daily-stats/all")
//    public ResponseEntity<AllRolesDailyStatsResponse> getAllRolesDailyStats(
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
//
//        if (month == null) {
//            month = YearMonth.now();
//        }

//        List<DailyVisitStatsDto> feStats = adminService.getDailyVisitStatsForFieldExecutive(
//                month.getMonthValue(),
//                month.getYear()
//        );

//        List<DailyVisitStatsDto> managerStats = adminService.getDailyVisitStatsForManager(
//                month.getMonthValue(),
//                month.getYear()
//        );
//
//        return ResponseEntity.ok(new AllRolesDailyStatsResponse(
//                month.getMonthValue(),
//                month.getYear(),
//                feStats,
//                managerStats
//        ));
//  }

    @GetMapping("/weekly-stats/all")
    public ResponseEntity<?> getAllRolesWeeklyStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
            if (month == null) {
                month = YearMonth.now();
            }

            List<WeeklyVisitStatsDto> feStats = adminService.getWeeklyVisitStatsForFieldExecutive(
                    month.getMonthValue(),
                    month.getYear()
            );

            List<WeeklyVisitStatsDto> managerStats = adminService.getWeeklyVisitStatsForManager(
                    month.getMonthValue(),
                    month.getYear()
            );

            return ResponseEntity.ok(new AllRolesWeeklyStatsResponse(

                    month.getMonthValue(),
                    month.getYear(),
                    feStats,
                    managerStats
            ));
    }


    @GetMapping("/counts")
    public ResponseEntity<AdminDashboardCounts> usersCount() {
        return ResponseEntity.ok(
                adminService.getCounts()
        );
    }

    @GetMapping("/get-all-zsm")
    public ResponseEntity<ApiResponseDto<List<ZsmResponseDto>>> getAllZsm() {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        adminService.getAllZsm(),
                        "Contact details fetched successfully"
                )
        );
    }

}
