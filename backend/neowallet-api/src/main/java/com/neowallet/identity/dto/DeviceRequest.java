package com.neowallet.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeviceRequest {

    @NotBlank
    private String deviceName;

    @NotBlank
    @Pattern(regexp = "ANDROID|IOS|WEB")
    private String deviceType;

    private String deviceToken;

    private String platform;

    private String osVersion;

    private String appVersion;

}
