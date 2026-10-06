package com.neowallet.identity.controller;

import com.neowallet.identity.dto.*;
import com.neowallet.identity.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userProfileService.getUserProfile(UUID.fromString(user.getUsername())));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal UserDetails user,
                                                          @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(UUID.fromString(user.getUsername()), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal UserDetails user,
                                                  @Valid @RequestBody DeleteUserRequest request) {
        userProfileService.deleteUser(UUID.fromString(user.getUsername()), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<UserPreferencesResponse> getUserPreferences(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userProfileService.getUserPreferences(UUID.fromString(user.getUsername())));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<UserPreferencesResponse> updateUserPreferences(@AuthenticationPrincipal UserDetails user,
                                                                         @Valid @RequestBody UpdateUserPreferencesRequest request) {
        return ResponseEntity.ok(userProfileService.updateUserPreferences(UUID.fromString(user.getUsername()), request));
    }

}
