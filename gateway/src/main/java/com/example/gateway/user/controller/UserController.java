package com.example.gateway.user.controller;

import com.example.gateway.item.dto.UserCreationRequestDto;
import com.example.gateway.item.dto.UserUpdateRequestDto;
import com.example.gateway.user.sevice.UserRemoteCommandImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRemoteCommandImpl command;


    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserCreationRequestDto userDto) {
        log.info("Received a POST request for the endpoint /users");
        return command.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Valid UserUpdateRequestDto userDto) {
        log.info("Received a PATCH request for the endpoint /users/{userId} with userId_{}", userId);
        return command.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Received a GET request for the endpoint /users/{userId} with userId_{}", userId);
        return command.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Received a GET request for the endpoint /users");
        return command.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Received a DELETE request for the endpoint /users/{userId} with userId_{}", userId);
        command.deleteUserById(userId);
    }
}
