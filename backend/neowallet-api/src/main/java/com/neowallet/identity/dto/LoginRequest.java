package com.neowallet.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String platform;
    private String osVersion;
    private String appVersion;

}
