package com.example.eidmock.dto;

public class AuthenticationSessionResponse {
    private String sessionID;

    public AuthenticationSessionResponse() {
    }

    public AuthenticationSessionResponse(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}

