package com.example.eidmock.service;

import com.example.eidmock.dto.MidSessionSignature;
import com.example.eidmock.dto.MidSessionStatus;
import com.example.eidmock.dto.MockConfiguration;
import com.example.eidmock.dto.SessionStatusResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionStore {

    private static final int SESSION_TIMEOUT_MINUTES = 5;

    @Autowired
    private RedisTemplate<String, SessionData> redisTemplate;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private MockConfigurationService mockConfigurationService;

    public static class SessionData implements Serializable {
        private static final long serialVersionUID = 1L;

        private String personalCode;
        private String country;
        private String hash;
        private String hashType;
        private String givenName;
        private String surname;
        private long createdAt;
        private int completionTimeoutSeconds;
        private boolean completed;
        private String certificateBase64;
        private String signature;
        private String endResult;

        public SessionData() {
            // Default constructor for Jackson
        }

        public SessionData(String personalCode, String country, String hash, String hashType, String givenName,
                String surname) {
            this.personalCode = personalCode;
            this.country = country;
            this.hash = hash;
            this.hashType = hashType != null ? hashType : "SHA512"; // Default to SHA512 for backward compatibility
            this.givenName = givenName;
            this.surname = surname;
            this.createdAt = System.currentTimeMillis();
            // Random timeout between 5 and 60 seconds
            this.completionTimeoutSeconds = 5 + (int) (Math.random() * 30);
            this.completed = false;
            this.endResult = "OK";
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public int getCompletionTimeoutSeconds() {
            return completionTimeoutSeconds;
        }

        public String getPersonalCode() {
            return personalCode;
        }

        public String getCountry() {
            return country;
        }

        public String getHash() {
            return hash;
        }

        public String getHashType() {
            return hashType;
        }

        public String getGivenName() {
            return givenName;
        }

        public String getSurname() {
            return surname;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getCertificateBase64() {
            return certificateBase64;
        }

        public void setCertificateBase64(String certificateBase64) {
            this.certificateBase64 = certificateBase64;
        }

        @JsonIgnore
        public X509Certificate getCertificate() throws Exception {
            if (certificateBase64 == null) {
                return null;
            }
            byte[] decoded = Base64.getDecoder().decode(certificateBase64);
            java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(decoded));
        }

        public void setCertificate(X509Certificate certificate) throws Exception {
            if (certificate != null) {
                this.certificateBase64 = Base64.getEncoder().encodeToString(certificate.getEncoded());
            }
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getEndResult() {
            return endResult;
        }

        public void setEndResult(String endResult) {
            this.endResult = endResult;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public void setCompletionTimeoutSeconds(int completionTimeoutSeconds) {
            this.completionTimeoutSeconds = completionTimeoutSeconds;
        }

        public void setPersonalCode(String personalCode) {
            this.personalCode = personalCode;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setHashType(String hashType) {
            this.hashType = hashType;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }

    public String createSession(String personalCode, String country, String hash, String hashType, String givenName,
            String surname) {
        String sessionId = UUID.randomUUID().toString();
        SessionData sessionData = new SessionData(personalCode, country, hash, hashType, givenName, surname);

        // Check for mock configuration
        MockConfiguration config = mockConfigurationService.getConfiguration(personalCode);
        if (config != null) {
            sessionData.setEndResult(config.getEndResult() != null ? config.getEndResult().name() : "OK");
            if (config.getDelaySeconds() != null && config.getDelaySeconds() > 0) {
                sessionData.setCompletionTimeoutSeconds(config.getDelaySeconds());
            }
        }

        redisTemplate.opsForValue().set(sessionId, sessionData, SESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        return sessionId;
    }

    public SessionData getSession(String sessionId) {
        return redisTemplate.opsForValue().get(sessionId);
    }

    public SessionData completeSession(String sessionId, X509Certificate certificate, String signature) {
        SessionData session = getSession(sessionId);
        if (session != null) {
            try {
                session.setCompleted(true);
                // Only set certificate and signature for successful results
                if ("OK".equals(session.getEndResult())) {
                    session.setCertificate(certificate);
                    session.setSignature(signature);
                }
                // Update in Redis
                redisTemplate.opsForValue().set(sessionId, session, SESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new RuntimeException("Failed to complete session", e);
            }
            return session;
        }
        return null;
    }

    public SessionStatusResponse getSessionStatus(String sessionId) {
        SessionData session = getSession(sessionId);
        if (session == null) {
            return null;
        }

        SessionStatusResponse response = new SessionStatusResponse();

        if (!session.isCompleted()) {
            // Check if timeout has elapsed
            long elapsedSeconds = (System.currentTimeMillis() - session.getCreatedAt()) / 1000;
            if (elapsedSeconds >= session.getCompletionTimeoutSeconds()) {
                // Auto-complete the session
                try {
                    // Only generate certificate for OK results
                    if ("OK".equals(session.getEndResult())) {
                        CertificateService.UserCertificateWithKey userCert = certificateService.generateUserCertificate(
                                session.getGivenName(),
                                session.getSurname(),
                                session.getPersonalCode(),
                                session.getCountry());
                        String signature = certificateService.signHash(session.getHash(), session.getHashType(),
                                userCert.getPrivateKey());
                        session = completeSession(sessionId, userCert.getCertificate(), signature);
                    } else {
                        // For non-OK results, just mark as completed
                        session = completeSession(sessionId, null, null);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to auto-complete session", e);
                }
            } else {
                response.setState("RUNNING");
                return response;
            }
        }

        response.setState("COMPLETE");
        response.setInteractionFlowUsed("displayTextAndPIN");

        SessionStatusResponse.Result result = new SessionStatusResponse.Result();
        result.setEndResult(session.getEndResult());
        result.setDocumentNumber("PNOEE-" + session.getPersonalCode());
        response.setResult(result);

        // Only include signature and certificate for successful results
        if ("OK".equals(session.getEndResult())) {
            SessionStatusResponse.Signature signature = new SessionStatusResponse.Signature();
            signature.setValue(session.getSignature());
            // Set algorithm based on hashType from session
            String algorithm = "SHA256".equalsIgnoreCase(session.getHashType())
                    ? "sha256WithRSAEncryption"
                    : "sha512WithRSAEncryption";
            signature.setAlgorithm(algorithm);
            response.setSignature(signature);

            SessionStatusResponse.Cert cert = new SessionStatusResponse.Cert();
            cert.setValue(session.getCertificateBase64());
            cert.setCertificateLevel("QUALIFIED");
            response.setCert(cert);
        }

        return response;
    }

    public MidSessionStatus getMidSessionStatus(String sessionId) {
        SessionData session = getSession(sessionId);
        if (session == null) {
            return null;
        }

        MidSessionStatus response = new MidSessionStatus();

        if (!session.isCompleted()) {
            // Check if timeout has elapsed
            long elapsedSeconds = (System.currentTimeMillis() - session.getCreatedAt()) / 1000;
            if (elapsedSeconds >= session.getCompletionTimeoutSeconds()) {
                // Auto-complete the session
                try {
                    // Only generate certificate for OK results
                    if ("OK".equals(session.getEndResult())) {
                        CertificateService.UserCertificateWithKey userCert = certificateService.generateUserCertificate(
                                session.getGivenName(),
                                session.getSurname(),
                                session.getPersonalCode(),
                                session.getCountry());
                        String signature = certificateService.signHash(session.getHash(), session.getHashType(),
                                userCert.getPrivateKey());
                        session = completeSession(sessionId, userCert.getCertificate(), signature);
                    } else {
                        // For non-OK results, just mark as completed
                        session = completeSession(sessionId, null, null);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to auto-complete session", e);
                }
            } else {
                response.setState("RUNNING");
                return response;
            }
        }

        response.setState("COMPLETE");
        response.setResult(session.getEndResult());

        // Only include signature and certificate for successful results
        if ("OK".equals(session.getEndResult())) {
            MidSessionSignature signature = new MidSessionSignature();
            signature.setValue(session.getSignature());
            // Set algorithm based on hashType from session
            String algorithm = "SHA256".equalsIgnoreCase(session.getHashType())
                    ? "sha256WithRSAEncryption"
                    : "sha512WithRSAEncryption";
            signature.setAlgorithm(algorithm);
            response.setSignature(signature);

            response.setCert(session.getCertificateBase64());
        }

        return response;
    }
}
