package com.neowallet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "neowallet.auth")
@Getter
@Setter
public class AuthProperties {

    private Duration accessTokenTtl = Duration.ofMinutes(15);
    private Duration refreshTokenTtl = Duration.ofDays(30);
    private Duration otpTtl = Duration.ofMinutes(10);
    private int otpMaxAttempts = 5;
    private int otpResendLimit = 3;
    private Duration otpResendWindow = Duration.ofMinutes(10);
    private int maxFailedLogins = 5;
    private Duration lockoutDuration = Duration.ofMinutes(30);
    private int maxDevices = 10;
    private int maxSessions = 10;
    private String jwtSecret;
    private String issuer = "neowallet-auth";
    private String audience = "neowallet-api";
    private String jwtAlgorithm = "HS256";

}
