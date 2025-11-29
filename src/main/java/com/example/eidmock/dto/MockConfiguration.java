package com.example.eidmock.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * Configuration for mock authentication behavior based on personal code
 */
public class MockConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private String personalCode;

    @Schema(description = "End result of authentication", example = "USER_REFUSED")
    private EndResult endResult;

    private Integer delaySeconds; // Custom delay before completion (if not set, uses random 5-60s)

    public MockConfiguration() {
    }

    public MockConfiguration(String personalCode, EndResult endResult, Integer delaySeconds) {
        this.personalCode = personalCode;
        this.endResult = endResult;
        this.delaySeconds = delaySeconds;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public EndResult getEndResult() {
        return endResult;
    }

    public void setEndResult(EndResult endResult) {
        this.endResult = endResult;
    }

    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }
}
