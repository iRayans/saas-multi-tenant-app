package com.rayan.saasapp.services;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.requests.UserRequest;
import com.rayan.saasapp.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void createUser(UserRequest request);

    void updateUser(String id, UserRequest request);

    void deleteUser(String id);

    UserResponse getUserById(String id);

    PageResponse<UserResponse> getAllUsers(int page, int size);

    void enableUser(String userId);

    void disableUser(String userId);
}
