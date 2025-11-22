package com.example.eidmock.dto;

public class AuthenticationSessionRequest {
    private String relyingPartyUUID;
    private String relyingPartyName;
    private String certificateLevel;
    private String hash;
    private String hashType;
    private String nonce;
    private AllowedInteractionsOrder[] allowedInteractionsOrder;
    private RequestProperties requestProperties;

    // Mobile-ID specific fields
    private String phoneNumber;
    private String nationalIdentityNumber;
    private String language;
    private String displayText;

    public static class AllowedInteractionsOrder {
        private String type;
        private String displayText60;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDisplayText60() {
            return displayText60;
        }

        public void setDisplayText60(String displayText60) {
            this.displayText60 = displayText60;
        }
    }

    public static class RequestProperties {
        private boolean shareMdClientIpAddress;

        public boolean isShareMdClientIpAddress() {
            return shareMdClientIpAddress;
        }

        public void setShareMdClientIpAddress(boolean shareMdClientIpAddress) {
            this.shareMdClientIpAddress = shareMdClientIpAddress;
        }
    }

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

    public String getCertificateLevel() {
        return certificateLevel;
    }

    public void setCertificateLevel(String certificateLevel) {
        this.certificateLevel = certificateLevel;
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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public AllowedInteractionsOrder[] getAllowedInteractionsOrder() {
        return allowedInteractionsOrder;
    }

    public void setAllowedInteractionsOrder(AllowedInteractionsOrder[] allowedInteractionsOrder) {
        this.allowedInteractionsOrder = allowedInteractionsOrder;
    }

    public RequestProperties getRequestProperties() {
        return requestProperties;
    }

    public void setRequestProperties(RequestProperties requestProperties) {
        this.requestProperties = requestProperties;
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
}

