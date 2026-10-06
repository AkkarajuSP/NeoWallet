package com.neowallet.provider;

public interface OtpProvider {

    void sendEmail(String email, String purpose, String plainOtp, String otpId);

    void sendSms(String phoneNumber, String purpose, String plainOtp, String otpId);

    boolean supports(String channel);

}
