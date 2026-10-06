package com.neowallet.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @NotBlank
    private String otpId;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")
    private String code;

    private String email;
    private String phoneNumber;

}
