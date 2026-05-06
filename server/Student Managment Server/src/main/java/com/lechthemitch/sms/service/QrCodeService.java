package com.lechthemitch.sms.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrCodeService {
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * Generate a QR code as PNG bytes from a token string.
     *
     * @param token the token to encode in the QR code
     * @param width the desired width in pixels
     * @param height the desired height in pixels
     * @return PNG image bytes
     * @throws IllegalStateException if QR generation fails
     */
    public byte[] generateQrCode(String token, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(token, BarcodeFormat.QR_CODE, width, height);
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            
            return pngOutputStream.toByteArray();
        } catch (IOException | com.google.zxing.WriterException e) {
            throw new IllegalStateException("Failed to generate QR code for token: " + token, e);
        }
    }

    /**
     * Generate a QR code with default dimensions (300x300).
     *
     * @param token the token to encode
     * @return PNG image bytes
     */
    public byte[] generateQrCode(String token) {
        return generateQrCode(token, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}

