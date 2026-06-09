package com.rayan.saasapp.auth.service;

import com.rayan.saasapp.auth.requests.LoginRequest;
import com.rayan.saasapp.auth.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(final LoginRequest loginRequest);
}
