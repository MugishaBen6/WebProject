package com.flight.management.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Verify2FARequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
} 