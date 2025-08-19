package com.flight.management.controllers;

import com.flight.management.model.Role;
import com.flight.management.model.User;
import com.flight.management.payload.*;
import com.flight.management.repositories.RoleRepository;
import com.flight.management.repositories.UserRepository;
import com.flight.management.security.JwtTokenProvider;
import com.flight.management.services.EmailService;
import com.flight.management.services.TwoFactorAuthenticationService;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TwoFactorAuthenticationService twoFactorService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Assign default USER role
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isTwoFactorEnabled()) {
            // Send 2FA code via email
            twoFactorService.sendTwoFactorCodeByEmail(user.getEmail());
            return ResponseEntity.ok(new JwtAuthResponse(jwt, true));
        }

        return ResponseEntity.ok(new JwtAuthResponse(jwt, false));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiry(System.currentTimeMillis() + 3600000); // 1 hour
            userRepository.save(user);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "Password reset instructions sent to your email");
            }});
        } catch (Exception e) {
            logger.error("Failed to process forgot password request", e);
            return ResponseEntity.badRequest().body("Failed to process forgot password request");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetPasswordTokenExpiry() < System.currentTimeMillis()) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<Enable2FAResponse> enable2FA(@RequestBody Enable2FARequest request) {
        try {
            String qrCodeImage = twoFactorService.enable2FA(request.getEmail());
            List<String> backupCodes = twoFactorService.getValidBackupCodes(request.getEmail());
            
            Enable2FAResponse response = new Enable2FAResponse(qrCodeImage, backupCodes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to enable 2FA", e);
            throw new RuntimeException("Failed to enable 2FA", e);
        }
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<?> verify2FA(@RequestBody Verify2FARequest request) {
        try {
            boolean isValid = twoFactorService.verify2FA(request.getEmail(), request.getCode());
            if (!isValid) {
                return ResponseEntity.badRequest().body("Invalid 2FA code");
            }
            return ResponseEntity.ok("2FA verified successfully");
        } catch (Exception e) {
            logger.error("Failed to verify 2FA code", e);
            return ResponseEntity.badRequest().body("Failed to verify 2FA code");
        }
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<?> disable2FA(@RequestBody Enable2FARequest request) {
        try {
            twoFactorService.disable2FA(request.getEmail());
            return ResponseEntity.ok("2FA disabled successfully");
        } catch (Exception e) {
            logger.error("Failed to disable 2FA", e);
            return ResponseEntity.badRequest().body("Failed to disable 2FA");
        }
    }

    @GetMapping("/2fa/backup-codes")
    public ResponseEntity<List<String>> getBackupCodes(@RequestParam String email) {
        try {
            List<String> backupCodes = twoFactorService.getValidBackupCodes(email);
            return ResponseEntity.ok(backupCodes);
        } catch (Exception e) {
            logger.error("Failed to get backup codes", e);
            throw new RuntimeException("Failed to get backup codes", e);
        }
    }
} 