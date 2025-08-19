package com.flight.management.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private String loadTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String template = loadTemplate("templates/password-reset-email.html");
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            String content = template.replace("{resetLink}", resetLink);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your Password");
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("Password reset email sent successfully to: {}", to);
        } catch (MessagingException | IOException e) {
            logger.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendTwoFactorCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String template = loadTemplate("templates/two-factor-email.html");
            String content = template.replace("{2faCode}", code);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Your Two-Factor Authentication Code");
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("2FA code email sent successfully to: {}", to);
        } catch (MessagingException | IOException e) {
            logger.error("Failed to send 2FA code email to: {}", to, e);
            throw new RuntimeException("Failed to send 2FA code email", e);
        }
    }
} 