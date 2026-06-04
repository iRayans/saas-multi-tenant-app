package com.rayan.saasapp.security;

import com.rayan.saasapp.config.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().contains("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && this.jwtService.validateToken(jwt)) {
                final String userId = this.jwtService.getUserIdFromToken(jwt);
                final String tenantId = this.jwtService.getTenantIdFromToken(jwt);
                final String role = this.jwtService.getRoleFromToken(jwt);

                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId);
                    final String schemaName = this.tenantSchemaResolver(tenantId);
                    TenantContext.setCurrentSchema(schemaName);
                }

                // Create authentication token
                final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("User authenticated for user ID: {}, role: {}", userId, authority.getAuthority());

            }
        } catch (Exception e) {
            log.error("Error authenticating user ", e);
        }
        filterChain.doFilter(request, response);
        TenantContext.clearCurrentTenant();

    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
