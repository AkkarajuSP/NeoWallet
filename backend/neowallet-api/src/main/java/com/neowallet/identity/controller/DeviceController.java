package com.neowallet.identity.controller;

import com.neowallet.identity.dto.DeviceRequest;
import com.neowallet.identity.dto.DeviceResponse;
import com.neowallet.identity.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> listDevices(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(deviceService.listDevices(UUID.fromString(user.getUsername())));
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> registerDevice(@AuthenticationPrincipal UserDetails user,
                                                         @Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(UUID.fromString(user.getUsername()), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeDevice(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable UUID id) {
        deviceService.revokeDevice(UUID.fromString(user.getUsername()), id);
        return ResponseEntity.noContent().build();
    }

}
