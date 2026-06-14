package com.rayan.saasapp.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Provides the current authenticated user to Spring Data JPA auditing,
 * automatically populating @CreatedBy and @LastModifiedBy entity fields on save.
 */
@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.equals("anonymousUser")) {
                return Optional.empty();
            }
            if (authentication.getPrincipal() != null) {
                return Optional.of((String) authentication.getPrincipal());
            }
            return Optional.empty();
        }
    }
}
