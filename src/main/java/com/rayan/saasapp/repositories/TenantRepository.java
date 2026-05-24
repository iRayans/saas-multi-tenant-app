package com.rayan.saasapp.repositories;

import com.rayan.saasapp.entites.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    boolean existsByEmail(String email);

    boolean existsByCompanyCode(String companyCode);
}
