package com.rayan.saasapp.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String tenantId = resolveHeader(request);

        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Tenant Id is missing in the request header, please add the header X-Tenant-Id.\"}");
            return;
        }
        try {
            TenantContext.setCurrentTenant(tenantId);
            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            TenantContext.clearCurrentTenant();
        }
    }

    private String resolveHeader(HttpServletRequest request) {
        final String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId.toLowerCase();
        }
        return null;
    }


}
