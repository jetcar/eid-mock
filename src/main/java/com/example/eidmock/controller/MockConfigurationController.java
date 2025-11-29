package com.example.eidmock.controller;

import com.example.eidmock.dto.EndResult;
import com.example.eidmock.dto.MockConfiguration;
import com.example.eidmock.service.MockConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mock-config")
@Tag(name = "Mock Configuration", description = "Configure mock authentication behavior based on personal codes")
public class MockConfigurationController {

    @Autowired
    private MockConfigurationService configService;

    @PostMapping("/{personalCode}/{endResult}")
    @Operation(summary = "Set mock configuration", description = "Configure how the mock service will respond for a specific personal code.")
    public ResponseEntity<MockConfiguration> setConfiguration(
            @Parameter(description = "Personal identification code", required = true) @PathVariable String personalCode,
            @Parameter(description = "End result of authentication", required = true) @PathVariable EndResult endResult,
            @Parameter(description = "Delay in seconds before completion") @RequestParam(required = false) Integer delaySeconds) {

        MockConfiguration config = new MockConfiguration(personalCode, endResult, delaySeconds);
        configService.setConfiguration(config);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{personalCode}")
    @Operation(summary = "Get mock configuration", description = "Retrieve the configured behavior for a personal code")
    public ResponseEntity<MockConfiguration> getConfiguration(
            @Parameter(description = "Personal identification code", required = true) @PathVariable String personalCode) {

        MockConfiguration config = configService.getConfiguration(personalCode);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/{personalCode}")
    @Operation(summary = "Delete mock configuration", description = "Remove the configured behavior for a personal code (will use default)")
    public ResponseEntity<Void> deleteConfiguration(
            @Parameter(description = "Personal identification code", required = true) @PathVariable String personalCode) {

        configService.deleteConfiguration(personalCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all configurations", description = "Get all configured personal codes and their behaviors")
    public ResponseEntity<List<MockConfiguration>> listConfigurations() {
        List<MockConfiguration> configs = configService.listConfigurations();
        return ResponseEntity.ok(configs);
    }

    @DeleteMapping
    @Operation(summary = "Clear all configurations", description = "Remove all mock configurations (all will use defaults)")
    public ResponseEntity<Void> clearAllConfigurations() {
        configService.clearAll();
        return ResponseEntity.noContent().build();
    }
}
