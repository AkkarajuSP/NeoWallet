package com.neowallet.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DeviceResponse {

    private UUID deviceId;
    private String deviceName;
    private String deviceType;
    private String platform;
    private String osVersion;
    private String appVersion;
    private Instant lastActiveAt;
    private Instant registeredAt;
    private boolean active;

}
