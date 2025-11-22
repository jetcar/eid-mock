package com.example.eidmock.dto;

public class SessionStatusResponse {
    private String state;
    private Result result;
    private Signature signature;
    private Cert cert;
    private String interactionFlowUsed;

    public static class Result {
        private String endResult;
        private String documentNumber;

        public Result() {
        }

        public Result(String endResult, String documentNumber) {
            this.endResult = endResult;
            this.documentNumber = documentNumber;
        }

        public String getEndResult() {
            return endResult;
        }

        public void setEndResult(String endResult) {
            this.endResult = endResult;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }
    }

    public static class Signature {
        private String value;
        private String algorithm;

        public Signature() {
        }

        public Signature(String value, String algorithm) {
            this.value = value;
            this.algorithm = algorithm;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }

    public static class Cert {
        private String value;
        private String certificateLevel;

        public Cert() {
        }

        public Cert(String value, String certificateLevel) {
            this.value = value;
            this.certificateLevel = certificateLevel;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getCertificateLevel() {
            return certificateLevel;
        }

        public void setCertificateLevel(String certificateLevel) {
            this.certificateLevel = certificateLevel;
        }
    }

    public SessionStatusResponse() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public Cert getCert() {
        return cert;
    }

    public void setCert(Cert cert) {
        this.cert = cert;
    }

    public String getInteractionFlowUsed() {
        return interactionFlowUsed;
    }

    public void setInteractionFlowUsed(String interactionFlowUsed) {
        this.interactionFlowUsed = interactionFlowUsed;
    }
}

