package com.neowallet.observability;

import org.springframework.context.annotation.Configuration;

/**
 * Secure logging configuration marker.
 * Logs should never include: passwords, OTP, tokens, secrets, or sensitive financial data.
 */
@Configuration
public class LoggingConfig {

    public static final String[] SENSITIVE_PATTERNS = {
        "password", "otp", "token", "secret", "apikey", "api_key",
        "authorization", "pan", "account_number", "card_number", "cvv"
    };

    public static boolean containsSensitiveField(String key) {
        String lower = key.toLowerCase();
        for (String pattern : SENSITIVE_PATTERNS) {
            if (lower.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

}
