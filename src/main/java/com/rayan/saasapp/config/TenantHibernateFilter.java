package com.rayan.saasapp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;

//@Aspect
//@Component
public class TenantHibernateFilter {
    @PersistenceContext
    private EntityManager entityManager;

    // Execute this method before any class within this package and nay class inside this package and any method with any parameters.
    @Before("execution(* com.rayan.saasapp.services.*.*(..))")
    public void activeFilter() {
        final String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            final Session session = entityManager.unwrap(Session.class);

            // Active the filter to inject the tenant id
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
        }
    }
}
