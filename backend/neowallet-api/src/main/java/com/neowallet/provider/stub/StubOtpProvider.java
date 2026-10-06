package com.neowallet.provider.stub;

import com.neowallet.provider.OtpProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "neowallet.otp", name = "provider", havingValue = "stub", matchIfMissing = true)
public class StubOtpProvider implements OtpProvider {

    @Override
    public void sendEmail(String email, String purpose, String plainOtp, String otpId) {
        log.info("[STUB OTP] email={} purpose={} otp={} otpId={}", mask(email), purpose, plainOtp, otpId);
    }

    @Override
    public void sendSms(String phoneNumber, String purpose, String plainOtp, String otpId) {
        log.info("[STUB OTP] phone={} purpose={} otp={} otpId={}", mask(phoneNumber), purpose, plainOtp, otpId);
    }

    @Override
    public boolean supports(String channel) {
        return true;
    }

    private String mask(String value) {
        if (value == null || value.length() < 4) {
            return "***";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }

}
