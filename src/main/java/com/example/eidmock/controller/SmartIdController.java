package com.example.eidmock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.eidmock.dto.AuthenticationSessionRequest;
import com.example.eidmock.dto.AuthenticationSessionResponse;
import com.example.eidmock.dto.SessionStatusResponse;
import com.example.eidmock.service.SessionStore;

@RestController
@Tag(name = "Smart-ID Mock API", description = "Mock implementation of Smart-ID API v2 for testing")
public class SmartIdController {

    private static final Logger log = LoggerFactory.getLogger(SmartIdController.class);

    @Autowired
    private SessionStore sessionStore;

    @PostMapping("/authentication/etsi/{personalCode}")
    @Operation(summary = "Start authentication by personal code", description = "Initiates a Smart-ID authentication session using national identity code in format PNOEE-12345678901")
    public ResponseEntity<AuthenticationSessionResponse> authenticateByPersonalCode(
            @Parameter(description = "Personal identification code with country prefix (e.g., PNOEE-40404040009)", required = true) @PathVariable String personalCode,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authentication session parameters") @RequestBody AuthenticationSessionRequest request) {

        // Parse country code from personalCode (format: PNOEE-38509076512)
        final String country;
        final String actualPersonalCode;

        if (personalCode != null && personalCode.startsWith("PNO") && personalCode.length() > 5) {
            // Extract country code (2 letters after PNO)
            country = personalCode.substring(3, 5);
            // Extract actual personal code after the dash
            int dashIndex = personalCode.indexOf('-');
            if (dashIndex > 0 && dashIndex < personalCode.length() - 1) {
                actualPersonalCode = personalCode.substring(dashIndex + 1);
            } else {
                actualPersonalCode = personalCode;
            }
        } else {
            country = "";
            actualPersonalCode = personalCode;
        }

        log.info("Authentication request for country: {}, personalCode: {}", country, actualPersonalCode);
        log.debug("Request: relyingPartyUUID={}, certificateLevel={}, hash={}",
                request.getRelyingPartyUUID(), request.getCertificateLevel(), request.getHash());

        // Extract names from personal code (mock data)
        String givenName = "MOCK";
        String surname = "USER-" + (actualPersonalCode != null
                ? actualPersonalCode.substring(Math.max(0, actualPersonalCode.length() - 4))
                : "0000");

        String sessionId = sessionStore.createSession(actualPersonalCode, country, request.getHash(), "SHA512",
                givenName,
                surname);

        log.info("Session {} created, will auto-complete after random timeout (5-60s)", sessionId);

        AuthenticationSessionResponse response = new AuthenticationSessionResponse(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get session status", description = "Retrieves the current status of a Smart-ID authentication session")
    public ResponseEntity<SessionStatusResponse> getSessionStatus(
            @Parameter(description = "Session identifier", required = true) @PathVariable String sessionId,
            @Parameter(description = "Timeout in milliseconds for long polling") @RequestParam(required = false) Long timeoutMs) {

        log.debug("Session status request for sessionId: {}, timeout: {}", sessionId, timeoutMs);

        // If timeout is specified, wait before checking status
        if (timeoutMs != null && timeoutMs > 0) {
            try {
                Thread.sleep(Math.min(timeoutMs, 3000)); // Wait max 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        SessionStatusResponse response = sessionStore.getSessionStatus(sessionId);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/certificatechoice/pno/{country}/{personalCode}")
    public ResponseEntity<?> getCertificateChoice(
            @PathVariable String country,
            @PathVariable String personalCode) {
        log.info("Certificate choice request for country: {}, personalCode: {}", country, personalCode);
        // This endpoint is used by the client to check if user exists
        // Return 404 if user doesn't exist, 200 if exists
        return ResponseEntity.ok().build();
    }
}
