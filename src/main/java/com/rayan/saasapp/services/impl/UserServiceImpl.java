package com.rayan.saasapp.services.impl;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.config.TenantContext;
import com.rayan.saasapp.entites.Tenant;
import com.rayan.saasapp.entites.User;
import com.rayan.saasapp.entites.enums.UserRole;
import com.rayan.saasapp.excpetions.DuplicateResourceException;
import com.rayan.saasapp.excpetions.InvalidRequestException;
import com.rayan.saasapp.mappers.UserMapper;
import com.rayan.saasapp.repositories.UserRepository;
import com.rayan.saasapp.requests.UserRequest;
import com.rayan.saasapp.response.UserResponse;
import com.rayan.saasapp.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();

        // validate if username already exists
        if (this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Validate if email already exists
        if (this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Validate role(cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("ROLE_PLATFORM_ADMIN is not allowed.");
        }

        final User user = this.mapper.toEntity(request);
        user.setTenant(Tenant.builder().id(tenantId).build());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        this.repository.save(user);
    }

    @Override
    public void updateUser(final String userId, final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Updating user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // check if username is being changed and if it is already taken
        if (!user.getUsername().equals(request.getUsername()) && this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if (!user.getEmail().equals(request.getEmail()) && this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("Role is required");
        }

        // update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        this.repository.save(user);
        log.info("User updated successfully");
    }

    @Override
    public void deleteUser(String id) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // Soft delete
        user.setDeleted(true);
        this.repository.save(user);
        log.info("User deleted successfully");
    }

    @Override
    public UserResponse getUserById(String id) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        return this.mapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Retrieving all users for tenant: {}", tenantId);
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<User> users = this.repository.findAllByTenantId(tenantId, pageRequest);
        final Page<UserResponse> response = users.map(this.mapper::toResponse);
        return PageResponse.of(response);
    }

    @Override
    public void enableUser(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Enabling user for tenant: {}", tenantId);
        User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }
        user.setEnabled(true);
        this.repository.save(user);
        log.info("User enabled successfully");
    }

    @Override
    public void disableUser(String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Disabling user for tenant: {}", tenantId);
        User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }
        user.setEnabled(false);
        this.repository.save(user);
        log.info("User disabled successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + username));
    }
}
