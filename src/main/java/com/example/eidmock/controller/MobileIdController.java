package com.example.eidmock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.eidmock.dto.AuthenticationSessionResponse;
import com.example.eidmock.dto.MidAuthenticationRequest;
import com.example.eidmock.dto.MidSessionStatus;
import com.example.eidmock.dto.MockConfiguration;
import com.example.eidmock.service.MockConfigurationService;
import com.example.eidmock.service.SessionStore;

@RestController
@RequestMapping("/mid-api")
@Tag(name = "Mobile-ID Mock API", description = "Mock implementation of Mobile-ID REST API for testing")
public class MobileIdController {

    private static final Logger log = LoggerFactory.getLogger(MobileIdController.class);

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private MockConfigurationService mockConfigurationService;

    @PostMapping("/authentication")
    @Operation(summary = "Start Mobile-ID authentication", description = "Initiates a Mobile-ID authentication session")
    public ResponseEntity<AuthenticationSessionResponse> startAuthentication(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Mobile-ID authentication request") @RequestBody MidAuthenticationRequest request) {

        String personalCode = request.getNationalIdentityNumber();
        String phoneNumber = request.getPhoneNumber();
        String hash = request.getHash();

        log.info("Mobile-ID authentication request for personalCode: {}, phoneNumber: {}", personalCode, phoneNumber);
        log.debug("Request: relyingPartyUUID={}, hash={}, displayText={}",
                request.getRelyingPartyUUID(), request.getHash(), request.getDisplayText());

        // Check if personal code has mock configuration
        MockConfiguration config = mockConfigurationService.getConfiguration(personalCode);
        if (config == null) {
            log.warn("No mock configuration found for personal code: {}", personalCode);
            return ResponseEntity.notFound().build();
        }

        // Extract names from personal code (mock data)
        String givenName = "MOBILE";
        String surname = "USER-" + (personalCode != null
                ? personalCode.substring(Math.max(0, personalCode.length() - 4))
                : "0000");
        String hashType = request.getHashType() != null ? request.getHashType() : "SHA256";

        String sessionId = sessionStore.createSession(personalCode, "EE", hash, hashType, givenName, surname);

        log.info("Mobile-ID session {} created, will auto-complete after random timeout (5-60s)", sessionId);

        AuthenticationSessionResponse response = new AuthenticationSessionResponse(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authentication/session/{sessionId}")
    @Operation(summary = "Get Mobile-ID session status", description = "Retrieves the current status of a Mobile-ID authentication session")
    public ResponseEntity<MidSessionStatus> getSessionStatus(
            @Parameter(description = "Session identifier", required = true) @PathVariable String sessionId,
            @Parameter(description = "Timeout in milliseconds for long polling") @RequestParam(required = false) Long timeoutMs) {

        log.debug("Mobile-ID session status request for sessionId: {}, timeout: {}", sessionId, timeoutMs);

        // If timeout is specified, wait before checking status
        if (timeoutMs != null && timeoutMs > 0) {
            try {
                Thread.sleep(Math.min(timeoutMs, 3000)); // Wait max 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        MidSessionStatus response = sessionStore.getMidSessionStatus(sessionId);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
