package com.example.eidmock.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mobile-ID authentication request")
public class MidAuthenticationRequest {

    @Schema(description = "Relying party UUID", example = "00000000-0000-0000-0000-000000000000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String relyingPartyUUID;

    @Schema(description = "Relying party name", example = "DEMO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String relyingPartyName;

    @Schema(description = "Phone number with country code", example = "+3726234566", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(description = "National identity number", example = "38412319871", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nationalIdentityNumber;

    @Schema(description = "Hash to be signed", example = "0nbgC2fVdLVQFZJdBbmG7oPoElpCYsQMtrY0c0wKYRg=", requiredMode = Schema.RequiredMode.REQUIRED)
    private String hash;

    @Schema(description = "Hash type algorithm", example = "SHA256", requiredMode = Schema.RequiredMode.REQUIRED)
    private String hashType;

    @Schema(description = "Language for user dialog", example = "ENG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Schema(description = "Text to display for verification", example = "This is display text.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String displayText;

    @Schema(description = "Format of the display text", example = "GSM-7", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String displayTextFormat;

    public String getRelyingPartyUUID() {
        return relyingPartyUUID;
    }

    public void setRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
    }

    public String getRelyingPartyName() {
        return relyingPartyName;
    }

    public void setRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationalIdentityNumber() {
        return nationalIdentityNumber;
    }

    public void setNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHashType() {
        return hashType;
    }

    public void setHashType(String hashType) {
        this.hashType = hashType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayTextFormat() {
        return displayTextFormat;
    }

    public void setDisplayTextFormat(String displayTextFormat) {
        this.displayTextFormat = displayTextFormat;
    }
}
