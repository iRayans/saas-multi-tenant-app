package com.rayan.saasapp.services.impl;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.entites.Tenant;
import com.rayan.saasapp.entites.User;
import com.rayan.saasapp.entites.enums.TenantStatus;
import com.rayan.saasapp.entites.enums.UserRole;
import com.rayan.saasapp.excpetions.DuplicateResourceException;
import com.rayan.saasapp.excpetions.InvalidRequestException;
import com.rayan.saasapp.mappers.TenantMapper;
import com.rayan.saasapp.repositories.TenantRepository;
import com.rayan.saasapp.repositories.UserRepository;
import com.rayan.saasapp.requests.RegisterTenantRequest;
import com.rayan.saasapp.response.TenantResponse;
import com.rayan.saasapp.services.ProvisioningService;
import com.rayan.saasapp.services.TenantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProvisioningService provisioningService;


    @Override
    public void registerTenant(RegisterTenantRequest request) {
        log.info("Register tenant request : {}", request.getCompanyName());

        if (this.tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }

        if (this.tenantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Tenant tenant = mapper.toEntity(request);
        tenant.setAdminPassword(passwordEncoder.encode(tenant.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void approveTenant(String tenantId) {
        // check if the tenant exists
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant doesn't exist"));
        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        try {
            // provision the Schema for the tenant
            this.provisioningService.provisionTenant(tenant);

            // Create the admin user for given tenant
            createAdminUser(tenant);
        } catch (Exception e) {
            rollbackTenantStaus(tenant);
        }
    }

    @Override
    public void activateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant doesn't exist"));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidRequestException("Tenant is not in pending status");
        }
        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant doesn't exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not in ACTIVE status");
        }
        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant doesn't exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not in ACTIVE status");
        }
        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);
    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenantPage = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenantPage.map((this.mapper::toResponse));
        return PageResponse.of(tenantResponses);
    }

    private void createAdminUser(final Tenant tenant) {
        // check if user already exist
        if (this.userRepository.existsByUsername(tenant.getAdminUsername())) {
            throw new DuplicateResourceException("Email already exists");
        }
        User adminUser = User.builder()
                .username(tenant.getAdminUsername())
                .firstName(extractFirstName(tenant.getAdminUsername()))
                .lastName(extractLastName(tenant.getAdminUsername()))
                .password(tenant.getAdminPassword())
                .email(tenant.getAdminEmail())
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .tenant(tenant)
                .deleted(false)
                .build();
        userRepository.save(adminUser);
        log.info("Admin user created successfully : {}", adminUser.getFirstName());
    }


    // Helper Methods
    private void rollbackTenantStaus(Tenant tenant) {
    }

    private String extractFirstName(final String adminFullName) {
        return adminFullName.split(" ")[0];
    }

    private String extractLastName(final String adminFullName) {
        return adminFullName.split(" ").length > 1 ? adminFullName.split(" ")[1] : adminFullName;
    }
}
