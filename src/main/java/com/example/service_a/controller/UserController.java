package com.example.service_a.controller;

import com.example.service_a.dto.UserDto;
import com.example.service_a.model.UserModel;
import com.example.service_a.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserModel userModel = toModel(userDto);
        UserModel savedModel = userService.createUser(userModel);
        return ResponseEntity.ok(toDto(savedModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@Valid @PathVariable String id) {
        UserModel userModel = userService.getUser(id);
        return ResponseEntity.ok(toDto(userModel));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserModel> userModels = userService.getAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (UserModel userModel : userModels) {
            userDtos.add(toDto(userModel));
        }
        return ResponseEntity.ok(userDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @PathVariable String id, @RequestBody UserDto userDto) {
        UserModel userModel = toModel(userDto);
        UserModel updatedModel = userService.updateUser(id, userModel);
        return ResponseEntity.ok(toDto(updatedModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserModel toModel(UserDto userDto) {
        return UserModel.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .createdAt(userDto.getCreatedAt())
                .build();
    }

    private UserDto toDto(UserModel userModel) {
        return UserDto.builder()
                .id(userModel.getId())
                .username(userModel.getUsername())
                .email(userModel.getEmail())
                .createdAt(userModel.getCreatedAt())
                .build();
    }
}