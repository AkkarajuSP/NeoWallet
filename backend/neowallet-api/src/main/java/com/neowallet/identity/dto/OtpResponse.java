package com.neowallet.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OtpResponse {

    private String otpId;
    private boolean verified;
    private Instant expiresAt;
    private int resendAfter;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

}
