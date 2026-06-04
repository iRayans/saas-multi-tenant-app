package com.rayan.saasapp.mappers;

import com.rayan.saasapp.entites.User;
import com.rayan.saasapp.requests.UserRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }
}
