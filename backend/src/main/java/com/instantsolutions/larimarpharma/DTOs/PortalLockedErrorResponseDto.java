package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PortalLockedErrorResponseDto {
    private String errorCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Object data;
}
