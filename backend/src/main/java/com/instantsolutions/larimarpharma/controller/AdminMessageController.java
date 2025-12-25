package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.AdminMessageRequestDto;
import com.instantsolutions.larimarpharma.entity.AdminMessage;
import com.instantsolutions.larimarpharma.service.AdminMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe/messages")
@RequiredArgsConstructor
public class AdminMessageController {

    private final AdminMessageService adminMessageService;

    // Send message to SuperAdmin
    @PostMapping("/super-admin/{superAdminId}")
    public ResponseEntity<AdminMessage> sendMessage(
            @RequestParam Long feId,
            @PathVariable Long superAdminId,
            @Valid @RequestBody AdminMessageRequestDto dto) {

        return ResponseEntity.ok(
                adminMessageService.sendMessageToSuperAdmin(feId, superAdminId, dto)
        );
    }

    // View my sent messages
    @GetMapping
    public ResponseEntity<List<AdminMessage>> getMyMessages(
            @RequestParam Long feId) {

        return ResponseEntity.ok(adminMessageService.getMyMessages(feId));
    }
}
