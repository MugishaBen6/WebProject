package com.flight.management.services;

import com.flight.management.model.BackupCode;
import com.flight.management.model.User;
import com.flight.management.repositories.BackupCodeRepository;
import com.flight.management.repositories.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class TwoFactorAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationService.class);
    private static final int BACKUP_CODE_LENGTH = 8;
    private static final int NUMBER_OF_BACKUP_CODES = 10;
    private static final int BACKUP_CODE_VALIDITY_DAYS = 180; // 6 months

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BackupCodeRepository backupCodeRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.2fa.issuer}")
    private String issuer;

    private final SecureRandom secureRandom = new SecureRandom();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(
        new DefaultCodeGenerator(),
        new SystemTimeProvider()
    );

    @Transactional
    public String enable2FA(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();

        QrData data = new QrData.Builder()
            .label(email)
            .secret(secret)
            .issuer(issuer)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            logger.error("Failed to generate QR code", e);
            throw new RuntimeException("Error generating QR code", e);
        }

        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        // Generate backup codes
        generateBackupCodes(user);

        return Base64.getEncoder().encodeToString(imageData);
    }

    @Transactional
    public boolean verify2FA(String email, String code) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled for this user");
        }

        // First try TOTP code
        if (codeVerifier.isValidCode(user.getTwoFactorSecret(), code)) {
            return true;
        }

        // If TOTP fails, try backup code
        Optional<BackupCode> backupCode = backupCodeRepository
            .findByUserAndCodeAndUsedFalseAndExpiresAtAfter(user, code, LocalDateTime.now());

        if (backupCode.isPresent()) {
            BackupCode validCode = backupCode.get();
            validCode.setUsed(true);
            backupCodeRepository.save(validCode);

            // If less than 3 backup codes remain, generate new ones
            if (backupCodeRepository.countValidBackupCodes(user, LocalDateTime.now()) < 3) {
                generateBackupCodes(user);
            }

            return true;
        }

        return false;
    }

    @Transactional
    public List<String> generateBackupCodes(User user) {
        // Delete existing unused backup codes
        LocalDateTime now = LocalDateTime.now();
        backupCodeRepository.deleteByUserAndExpiresAtBefore(user, now);

        List<String> backupCodes = new ArrayList<>();
        LocalDateTime expiryDate = now.plusDays(BACKUP_CODE_VALIDITY_DAYS);

        for (int i = 0; i < NUMBER_OF_BACKUP_CODES; i++) {
            String code = generateBackupCode();
            backupCodes.add(code);
            backupCodeRepository.save(new BackupCode(user, code, expiryDate));
        }

        return backupCodes;
    }

    private String generateBackupCode() {
        StringBuilder code = new StringBuilder();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i = 0; i < BACKUP_CODE_LENGTH; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        
        return code.toString();
    }

    @Transactional
    public void disable2FA(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        // Delete all backup codes
        backupCodeRepository.deleteByUserAndExpiresAtBefore(user, LocalDateTime.now().plusYears(100));
    }

    @Transactional(readOnly = true)
    public List<String> getValidBackupCodes(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return backupCodeRepository.findByUserAndUsedFalseAndExpiresAtAfter(user, LocalDateTime.now())
            .stream()
            .map(BackupCode::getCode)
            .toList();
    }

    @Transactional
    public void sendTwoFactorCodeByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled for this user");
        }

        try {
            CodeGenerator codeGenerator = new DefaultCodeGenerator();
            long currentTime = System.currentTimeMillis() / 30000L; // Current 30-second window
            String code = codeGenerator.generate(user.getTwoFactorSecret(), currentTime);
            emailService.sendTwoFactorCode(email, code);
        } catch (CodeGenerationException e) {
            logger.error("Failed to generate 2FA code", e);
            throw new RuntimeException("Failed to generate 2FA code", e);
        }
    }
} 