// AuthController.java
package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.LoginResponseDto;
import com.instantsolutions.larimarpharma.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @RequestParam String userEmail , @RequestParam String userPassword) {

        log.info("Login request received for email: {}", userEmail);

        try {
            LoginResponseDto response = authService.authenticate(userEmail , userPassword);

            log.info("Login successful for user: {}", response.getEmail());

            return ResponseEntity.ok(
                    ApiResponseDto.success(response, "Login successful")
            );

        } catch (Exception e) {
            log.error("Login failed for email: {}. Error: {}",
                    userEmail, e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
        }
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ApiResponseDto<LoginResponseDto>> refreshToken(
//            @RequestParam String refreshToken) {
//
//        log.info("Refresh token request received");
//
//        try {
//            LoginResponseDto response = authService.refreshToken(refreshToken);
//
//            return ResponseEntity.ok(
//                    ApiResponseDto.success(response, "Token refreshed successfully")
//            );
//
//        } catch (Exception e) {
//            log.error("Token refresh failed. Error: {}", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(ApiResponseDto.error("Invalid refresh token", HttpStatus.UNAUTHORIZED.value()));
//        }
//    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("Logout request received");

        // In production, you would blacklist the token
        // For now, just return success

        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Logout successful")
        );
    }

//    @GetMapping("/validate")
//    public ResponseEntity<ApiResponseDto<Boolean>> validateToken(
//            @RequestParam String token,
//            @RequestParam String userType) {
//
//        try {
//            boolean isValid = authService.validateToken(token, userType);
//
//            return ResponseEntity.ok(
//                    ApiResponseDto.success(isValid, "Token validation completed")
//            );
//
//        } catch (Exception e) {
//            log.error("Token validation failed. Error: {}", e.getMessage());
//
//            return ResponseEntity.ok(
//                    ApiResponseDto.success(false, "Token validation failed")
//            );
//        }
//    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponseDto<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponseDto.success("Auth service is running", "Service is healthy")
        );
    }
}