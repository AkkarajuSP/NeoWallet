package com.neowallet.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpRequest {

    @Email
    private String email;

    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "REGISTRATION|LOGIN|RESET")
    private String purpose;

}
