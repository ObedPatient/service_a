package com.example.service_a.controller;

import com.example.service_a.dto.UserFlatDto;
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
    public ResponseEntity<UserFlatDto> createUser(@Valid @RequestBody UserFlatDto userDto) {
        UserModel userModel = toModel(userDto);
        UserModel savedModel = userService.createUser(userModel);
        return ResponseEntity.ok(toDto(savedModel));
    }

    @GetMapping("/{performerId}")
    public ResponseEntity<UserFlatDto> getUser(@Valid @PathVariable String performerId) {
        UserModel userModel = userService.getUser(performerId);
        return ResponseEntity.ok(toDto(userModel));
    }

    @GetMapping
    public ResponseEntity<List<UserFlatDto>> getAllUsers() {
        List<UserModel> userModels = userService.getAllUsers();
        List<UserFlatDto> userDtos = new ArrayList<>();
        for (UserModel userModel : userModels) {
            userDtos.add(toDto(userModel));
        }
        return ResponseEntity.ok(userDtos);
    }

    @PutMapping("/{performerId}")
    public ResponseEntity<UserFlatDto> updateUser(@Valid @PathVariable String performerId, @RequestBody UserFlatDto userDto) {
        UserModel userModel = toModel(userDto);
        UserModel updatedModel = userService.updateUser(performerId, userModel);
        return ResponseEntity.ok(toDto(updatedModel));
    }

    @DeleteMapping("/{performerId}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable String performerId) {
        userService.deleteUser(performerId);
        return ResponseEntity.noContent().build();
    }

    private UserModel toModel(UserFlatDto userDto) {
        return UserModel.builder()
                .performerId(userDto.getPerformerId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .workEmail(userDto.getWorkEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    private UserFlatDto toDto(UserModel userModel) {
        return UserFlatDto.builder()
                .performerId(userModel.getPerformerId())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .workEmail(userModel.getWorkEmail())
                .phoneNumber(userModel.getPhoneNumber())
                .build();
    }
}