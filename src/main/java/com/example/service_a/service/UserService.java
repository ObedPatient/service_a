package com.example.service_a.service;

import com.example.service_a.model.UserModel;
import com.example.service_a.repository.UserRepository;
import com.example.service_a.util.UserIdGenerator;
import com.example.service_a.component.Logger;
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
        if (userRepository.existsByWorkEmail(userModel.getWorkEmail())) {
            throw new RuntimeException("Email already exists");
        }
        UserModel newUser = UserModel.builder()
                .performerId(UserIdGenerator.generateId())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .workEmail(userModel.getWorkEmail())
                .phoneNumber(userModel.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    public UserModel getUser(String performerId) {
        return userRepository.findByPerformerId(performerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel updateUser(String performerId, UserModel userModel) {
        UserModel existingUser = userRepository.findByPerformerId(performerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userRepository.existsByWorkEmail(userModel.getWorkEmail()) &&
                !existingUser.getWorkEmail().equals(userModel.getWorkEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existingUser.setFirstName(userModel.getFirstName());
        existingUser.setLastName(userModel.getLastName());
        existingUser.setWorkEmail(userModel.getWorkEmail());
        existingUser.setPhoneNumber(userModel.getPhoneNumber());
        return userRepository.save(existingUser);
    }

    public void deleteUser(String performerId) {
        if (!userRepository.existsByPerformerId(performerId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteByPerformerId(performerId);
    }
}