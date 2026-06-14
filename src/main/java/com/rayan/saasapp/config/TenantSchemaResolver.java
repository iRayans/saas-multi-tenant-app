package com.rayan.saasapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSchemaResolver {
    private final JdbcTemplate jdbcTemplate;
    private static final String PUBLIC_SCHEMA = "public";


    /**
     * Resolves and caches the schema name for the given tenant ID.
     * Cached by tenantId to avoid repeated DB lookups on every request.
     * Returns "public" if the tenant is not found or an error occurs.
     */
    @Cacheable(value = "tenantSchema", key = "#tenantId", condition = "#result != 'public'")
    public String resolveTenantSchema(final String tenantId) {
        if (tenantId == null) {
            return PUBLIC_SCHEMA;
        }

        try {
            final String companyCode = this.jdbcTemplate.queryForObject(
                    "SELECT company_code FROM public.tenants WHERE id = ? and deleted = false", String.class, tenantId);

            if (companyCode != null) {
                final String schemaName = "tenant_" + companyCode.toLowerCase();
                log.debug("Tenant Schema resolved: {} for tenant {} ", schemaName, tenantId);
                return schemaName;
            }
            log.warn("Tenant Schema not found: {}, using public schema ", tenantId);
            return PUBLIC_SCHEMA;
        } catch (Exception e) {
            log.error("Error resolving tenant schema for tenant {}", tenantId, e);
            return PUBLIC_SCHEMA;
        }
    }
}
