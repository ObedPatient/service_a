package com.example.service_a.repository;

import com.example.service_a.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    boolean existsByUsernameOrEmail(String username, String email);
}