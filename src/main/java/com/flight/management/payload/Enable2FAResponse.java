package com.flight.management.payload;

import java.util.List;

public class Enable2FAResponse {
    private String qrCodeImage;
    private List<String> backupCodes;

    public Enable2FAResponse(String qrCodeImage, List<String> backupCodes) {
        this.qrCodeImage = qrCodeImage;
        this.backupCodes = backupCodes;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public List<String> getBackupCodes() {
        return backupCodes;
    }

    public void setBackupCodes(List<String> backupCodes) {
        this.backupCodes = backupCodes;
    }
} 