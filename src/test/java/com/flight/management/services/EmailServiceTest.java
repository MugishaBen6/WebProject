package com.flight.management.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:3000");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_ShouldSendEmail() throws MessagingException {
        // Arrange
        String toEmail = "user@example.com";
        String resetToken = "test-reset-token";

        // Act
        emailService.sendPasswordResetEmail(toEmail, resetToken);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendTwoFactorCode_ShouldSendEmail() throws MessagingException {
        // Arrange
        String toEmail = "user@example.com";
        String code = "123456";

        // Act
        emailService.sendTwoFactorCode(toEmail, code);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
} 