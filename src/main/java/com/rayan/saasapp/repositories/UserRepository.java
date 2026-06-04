package com.rayan.saasapp.repositories;

import com.rayan.saasapp.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM USER u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdAndNotDeleted(String id);
}
