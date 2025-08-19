package com.flight.management.payload;

public class JwtAuthResponse {
    private String accessToken;
    private boolean requiresTwoFactor;

    public JwtAuthResponse(String accessToken, boolean requiresTwoFactor) {
        this.accessToken = accessToken;
        this.requiresTwoFactor = requiresTwoFactor;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isRequiresTwoFactor() {
        return requiresTwoFactor;
    }

    public void setRequiresTwoFactor(boolean requiresTwoFactor) {
        this.requiresTwoFactor = requiresTwoFactor;
    }
} 