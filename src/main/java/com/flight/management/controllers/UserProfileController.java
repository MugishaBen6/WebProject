package com.flight.management.controllers;

import com.flight.management.model.User;
import com.flight.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<User> getProfile(Principal principal) {
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(Principal principal, @RequestBody User updated) {
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userOpt.get();
        user.setFullName(updated.getFullName());
        user.setEmail(updated.getEmail());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        userService.createUser(user); // save updated user
        return ResponseEntity.ok(user);
    }
} 