package com.rayan.saasapp.config;

public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_SCHEMA = new ThreadLocal<>();


    // TENANT
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static void clearCurrentTenant() {
        CURRENT_TENANT.remove();
        CURRENT_SCHEMA.remove();
    }

    // SCHEMA
    public static String getCurrentSchema() {
        return CURRENT_SCHEMA.get();
    }

    public static void setCurrentSchema(String tenant) {
        CURRENT_SCHEMA.set(tenant);
    }
}
