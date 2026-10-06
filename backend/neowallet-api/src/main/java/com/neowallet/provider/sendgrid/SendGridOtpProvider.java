package com.neowallet.provider.sendgrid;

import com.neowallet.provider.OtpProvider;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "neowallet.otp", name = "provider", havingValue = "sendgrid")
public class SendGridOtpProvider implements OtpProvider {

    private final SendGrid sendGrid;
    private final String fromEmail;

    public SendGridOtpProvider(
            @Value("${SENDGRID_API_KEY}") String apiKey,
            @Value("${SENDGRID_FROM_EMAIL}") String fromEmail) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("SENDGRID_API_KEY must be configured");
        }
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalArgumentException("SENDGRID_FROM_EMAIL must be configured");
        }
        this.sendGrid = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendEmail(String email, String purpose, String plainOtp, String otpId) {
        Email from = new Email(fromEmail);
        Email to = new Email(email);
        String subject = "NeoWallet " + purpose + " verification code";
        String body = "Your NeoWallet " + purpose + " verification code is: " + plainOtp;
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response == null) {
                throw new IllegalStateException("SendGrid response was null");
            }

            int status = response.getStatusCode();
            if (status < 200 || status >= 300) {
                log.error("SendGrid OTP email submission failed: status={} otpId={}", status, otpId);
                throw new IllegalStateException("SendGrid returned HTTP " + status);
            }

            log.info("SendGrid OTP email submitted: status={} otpId={}", status, otpId);
        } catch (IOException e) {
            log.error("SendGrid OTP email submission failed: otpId={}", otpId, e);
            throw new IllegalStateException("Failed to send OTP via SendGrid", e);
        }
    }

    @Override
    public void sendSms(String phoneNumber, String purpose, String plainOtp, String otpId) {
        log.warn("SendGrid provider does not support SMS OTP delivery");
    }

    @Override
    public boolean supports(String channel) {
        return "email".equalsIgnoreCase(channel);
    }

}
