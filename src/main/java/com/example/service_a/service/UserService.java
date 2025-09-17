package com.example.service_a.service;

import com.example.service_a.model.UserModel;
import com.example.service_a.repository.UserRepository;
import com.example.service_a.util.UserIdGenerator;
import com.example.service_a.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Logger logger;

    public UserModel createUser(UserModel userModel) {
        if (userRepository.existsByUsernameOrEmail(userModel.getUsername(), userModel.getEmail())) {
            throw new RuntimeException("Username or email already exists");
        }
        UserModel newUser = UserModel.builder()
                .id(UserIdGenerator.generateId())
                .username(userModel.getUsername())
                .email(userModel.getEmail())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    public UserModel getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel updateUser(String id, UserModel userModel) {
        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userRepository.existsByUsernameOrEmail(userModel.getUsername(), userModel.getEmail()) &&
                !existingUser.getUsername().equals(userModel.getUsername()) &&
                !existingUser.getEmail().equals(userModel.getEmail())) {
            throw new RuntimeException("Username or email already exists");
        }
        existingUser.setUsername(userModel.getUsername());
        existingUser.setEmail(userModel.getEmail());
        return userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}