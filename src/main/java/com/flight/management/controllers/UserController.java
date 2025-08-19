package com.flight.management.controllers;

import com.flight.management.model.User;
import com.flight.management.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get paginated users", description = "Retrieves a paginated list of users")
    @GetMapping("/paged")
    public ResponseEntity<Page<User>> getAllUsersPaged(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Search users", description = "Search users with pagination")
    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(userService.findBySearchCriteria(query, pageable));
    }

    @Operation(summary = "Quick search users", description = "Quick search users by email or name")
    @GetMapping("/search/quick")
    public ResponseEntity<List<User>> quickSearch(
            @Parameter(description = "Search query") @RequestParam String query) {
        return ResponseEntity.ok(userService.findByEmailOrName(query));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create user", description = "Creates a new user")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User details") @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @Operation(summary = "Update user", description = "Updates an existing user")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Updated user details") @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete user", description = "Deletes a user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user by email", description = "Retrieves a specific user by their email")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email of the user") @PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}