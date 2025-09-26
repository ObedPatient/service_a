package com.example.service_a.repository;

import com.example.service_a.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, String> {
    boolean existsByWorkEmail(String workEmail);
    Optional<UserModel> findByPerformerId(String performerId);
    boolean existsByPerformerId(String performerId);
    void deleteByPerformerId(String performerId);
}