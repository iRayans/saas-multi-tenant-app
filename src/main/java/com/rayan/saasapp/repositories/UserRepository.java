package com.rayan.saasapp.repositories;

import com.rayan.saasapp.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
