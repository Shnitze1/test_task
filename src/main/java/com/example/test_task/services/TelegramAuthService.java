package com.example.test_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.test_task.utils.SameMethods.parseInitData;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    public boolean validateInitData(String initData) {
        if (initData == null || initData.isBlank()) {
            log.warn("Validation failed: initData is empty");
            return false;
        }

        try {
            Map<String, String> params = parseInitData(initData);

            String receivedHash = params.get("hash");
            if (receivedHash == null) {
                log.warn("Validation failed: Hash is missing");
                return false;
            }
            params.remove("hash");

            String dataString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("\n"));

            String expectedHash = calculateTelegramHash(dataString);

            return expectedHash.equals(receivedHash);
        } catch (Exception e) {
            log.error("Telegram validation error: {}", e.getMessage());
            return false;
        }
    }

    private String calculateTelegramHash(String dataString) throws NoSuchAlgorithmException, InvalidKeyException {

        byte[] secretKey = generateHmac("WebAppData".getBytes(StandardCharsets.UTF_8), botToken.getBytes(StandardCharsets.UTF_8));
        byte[] hashBytes = generateHmac(secretKey, dataString.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hashBytes);
    }

    private byte[] generateHmac(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(key, "HmacSHA256"));
        return hmac.doFinal(data);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}