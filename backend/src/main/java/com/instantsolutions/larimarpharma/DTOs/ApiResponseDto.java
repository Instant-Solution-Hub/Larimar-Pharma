
// ApiResponseDto.java
package com.instantsolutions.larimarpharma.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    private Integer status;

    public ApiResponseDto(boolean success, String message, T data, Integer status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }

    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, message, data, 200);
    }

    public static <T> ApiResponseDto<T> error(String message, Integer status) {
        return new ApiResponseDto<>(false, message, null, status);
    }
}