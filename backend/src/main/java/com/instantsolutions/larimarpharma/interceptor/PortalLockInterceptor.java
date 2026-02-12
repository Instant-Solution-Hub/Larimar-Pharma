package com.instantsolutions.larimarpharma.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instantsolutions.larimarpharma.DTOs.UserIdentityDto;
import com.instantsolutions.larimarpharma.service.PortalLockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class PortalLockInterceptor implements HandlerInterceptor {

    @Autowired
    private PortalLockService portalLockService;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_TYPE_HEADER = "X-User-Type";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // Skip check for unlock request endpoints and auth
        String requestUri = request.getRequestURI();
        if (shouldSkipCheck(requestUri)) {
            return true;
        }

        // Extract user info from headers
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userTypeHeader = request.getHeader(USER_TYPE_HEADER);

        // If headers are not present, try to extract from request parameters
        if (userIdHeader == null || userTypeHeader == null) {
            userIdHeader = request.getParameter("userId");
            userTypeHeader = request.getParameter("userType");
        }

        // If still not found, allow the request (could be public endpoint)
        if (userIdHeader == null || userTypeHeader == null) {
            return true;
        }

        try {
            Long userId = Long.parseLong(userIdHeader);

            // Create a simple DTO-like object
            UserIdentityDto userIdentifier = UserIdentityDto.builder()
                    .userId(userId)
                    .userType(userTypeHeader)
                    .build();

            if (portalLockService.isPortalLocked(userIdentifier)) {
                sendPortalLockedResponse(response);
                return false;
            }
        } catch (NumberFormatException e) {
            // Invalid user ID format, allow request but log
            return true;
        } catch (Exception e) {
            // Any other exception, allow request but log
            return true;
        }

        return true;
    }

    private boolean shouldSkipCheck(String requestUri) {
        return requestUri.contains("/api/portal/request-unlock") ||
                requestUri.contains("/api/portal/status") ||
                requestUri.contains("/api/auth") ||
                requestUri.contains("/admin/portal/unlock-requests") ||
                requestUri.contains("/swagger") ||
                requestUri.contains("/v3/api-docs") ||
                requestUri.contains("/actuator") ||
                requestUri.equals("/error");
    }

    private void sendPortalLockedResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "PORTAL_LOCKED");
        errorResponse.put(
                "message",
                "Your portal is locked. Please request unlock from admin."
        );
        errorResponse.put("timestamp", LocalDateTime.now());

        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(errorResponse));
    }


}