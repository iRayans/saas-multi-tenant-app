package com.rayan.saasapp.mappers;

import com.rayan.saasapp.entites.Tenant;
import com.rayan.saasapp.requests.RegisterTenantRequest;
import com.rayan.saasapp.response.TenantResponse;

import java.time.LocalDateTime;

public class TenantMapper {

    public Tenant toEntity(RegisterTenantRequest request) {
        return Tenant.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .createdAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .email(request.getEmail())
                .adminPassword(request.getAdminPassword())
                .adminFullName(request.getAdminFullName())
                .adminEmail(request.getAdminEmail())
                .deleted(false)
                .build();
    }

    public TenantResponse toResponse(Tenant entity) {
        return TenantResponse.builder()
                .tenantId(entity.getId())
                .companyName(entity.getCompanyName())
                .companyCode(entity.getCompanyCode())
                .email(entity.getEmail())
                .adminFullName(entity.getAdminFullName())
                .adminEmail(entity.getAdminEmail())
                .build();
    }
}
