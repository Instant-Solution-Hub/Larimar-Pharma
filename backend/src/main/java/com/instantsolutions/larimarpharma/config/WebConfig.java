package com.instantsolutions.larimarpharma.config;

import com.instantsolutions.larimarpharma.interceptor.PortalLockInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Autowired
//    private PortalLockInterceptor portalLockInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // Apply to all endpoints except excluded ones
//        registry.addInterceptor(portalLockInterceptor)
//                .addPathPatterns("/api/**")
//                .excludePathPatterns(
//                        "/api/portal/request-unlock",
//                        "/api/portal/status",
//                        "/api/auth/**",
//                        "/api/portal/admin/**",
//                        "/api/admin/**",
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**",
//                        "/api/v1/auth/**"
//                );
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
    }
}
