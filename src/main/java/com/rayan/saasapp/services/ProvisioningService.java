package com.rayan.saasapp.services;

import com.rayan.saasapp.entites.Tenant;

public interface ProvisioningService {
    void provisionTenant(final Tenant tenant);
}
