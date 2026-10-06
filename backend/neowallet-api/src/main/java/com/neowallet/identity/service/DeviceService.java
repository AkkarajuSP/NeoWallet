package com.neowallet.identity.service;

import com.neowallet.config.AuthProperties;
import com.neowallet.identity.dto.DeviceRequest;
import com.neowallet.identity.dto.DeviceResponse;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.entity.UserDevice;
import com.neowallet.identity.repository.UserDeviceRepository;
import com.neowallet.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final AuthProperties authProperties;
    private final UserDeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<DeviceResponse> listDevices(UUID userId) {
        return deviceRepository.findByUserUserIdAndIsActiveTrue(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public DeviceResponse registerDevice(UUID userId, DeviceRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int activeCount = deviceRepository.countByUserUserIdAndIsActiveTrue(userId);
        if (activeCount >= authProperties.getMaxDevices()) {
            throw new IllegalStateException("Maximum device limit reached");
        }

        UserDevice device = new UserDevice();
        device.setUser(user);
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType().toUpperCase());
        device.setDeviceToken(request.getDeviceToken());
        device.setPlatform(request.getPlatform());
        device.setOsVersion(request.getOsVersion());
        device.setAppVersion(request.getAppVersion());
        device.setLastActiveAt(Instant.now());
        device.setIsActive(true);

        device = deviceRepository.save(device);

        auditService.recordAuthentication(userId, "DEVICE_REGISTERED", device.getDeviceId(), true, "");

        return toResponse(device);
    }

    @Transactional
    public void revokeDevice(UUID userId, UUID deviceId) {
        UserDevice device = deviceRepository.findByDeviceIdAndUserUserId(deviceId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        device.setIsActive(false);
        device.setLastActiveAt(Instant.now());
        deviceRepository.save(device);

        auditService.recordAuthentication(userId, "DEVICE_REVOKED", device.getDeviceId(), true, "");
    }

    private DeviceResponse toResponse(UserDevice device) {
        return DeviceResponse.builder()
            .deviceId(device.getDeviceId())
            .deviceName(device.getDeviceName())
            .deviceType(device.getDeviceType())
            .platform(device.getPlatform())
            .osVersion(device.getOsVersion())
            .appVersion(device.getAppVersion())
            .lastActiveAt(device.getLastActiveAt())
            .registeredAt(device.getCreatedAt())
            .active(device.getIsActive())
            .build();
    }

}
