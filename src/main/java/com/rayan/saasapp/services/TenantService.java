package com.rayan.saasapp.services;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.requests.RegisterTenantRequest;
import com.rayan.saasapp.response.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest request);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);


}
