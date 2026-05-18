package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.request.UserRequest;
import com.example.sheabutterledgersystem.dto.response.UserResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.UserMapper;
import com.example.sheabutterledgersystem.model.User;
import com.example.sheabutterledgersystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing system users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<User> userPage = userService.getAllUsers(page, size, sortBy, sortDir);
        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> userPage = userService.searchUsers(q, page, size);
        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {

        User userDetails = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {

        User updatedUser = userService.updateUserStatus(id, active);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(Map.of(
                "activeUsers", userService.getActiveUserCount(),
                "inactiveUsers", userService.getInactiveUserCount(),
                "totalUsers", userService.getActiveUserCount() + userService.getInactiveUserCount()
        ));
    }
}