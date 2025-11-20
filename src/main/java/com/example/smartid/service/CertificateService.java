package com.example.smartid.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Service
public class CertificateService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private PrivateKey caPrivateKey;
    private X509Certificate caCertificate;

    @Value("${ca.keystore.path}")
    private String keystorePath;

    @Value("${ca.keystore.password}")
    private String keystorePassword;

    @Value("${ca.keystore.key-alias}")
    private String keyAlias;

    @Value("${ca.keystore.cert-alias}")
    private String certAlias;

    public CertificateService() {
    }

    private void loadCertificate() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keystoreStream = new FileSystemResource(keystorePath).getInputStream()) {
            keyStore.load(keystoreStream, keystorePassword.toCharArray());
        }

        // Load certificate from cert alias
        caCertificate = (X509Certificate) keyStore.getCertificate(certAlias);
        if (caCertificate == null) {
            throw new RuntimeException("CA certificate not found with alias: " + certAlias);
        }

        // Load private key from key alias
        Key key = keyStore.getKey(keyAlias, keystorePassword.toCharArray());
        if (!(key instanceof PrivateKey)) {
            throw new RuntimeException("Private key not found with alias: " + keyAlias);
        }
        caPrivateKey = (PrivateKey) key;
    }

    private void ensureInitialized() {
        if (caPrivateKey == null || caCertificate == null) {
            try {
                loadCertificate();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load CA certificate", e);
            }
        }
    }

    public static class UserCertificateWithKey {
        private final X509Certificate certificate;
        private final PrivateKey privateKey;

        public UserCertificateWithKey(X509Certificate certificate, PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }
    }

    public UserCertificateWithKey generateUserCertificate(String givenName, String surname, String personalCode,
            String country) throws Exception {
        ensureInitialized();

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair userKeyPair = keyGen.generateKeyPair();

        X500Name issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        X500Name subject = new X500Name(String.format("CN=%s %s, SERIALNUMBER=PNO%s-%s, C=%s",
                givenName, surname, country.toUpperCase(), personalCode, country.toUpperCase()));

        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(userKeyPair.getPublic().getEncoded());

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                subject,
                publicKeyInfo);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA")
                .setProvider("BC")
                .build(caPrivateKey);

        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);

        return new UserCertificateWithKey(certificate, (PrivateKey) userKeyPair.getPrivate());
    }

    public String signHash(String hashInBase64, PrivateKey privateKey) throws Exception {
        // The hash is already computed (SHA-512), so we sign it directly without
        // additional hashing
        // We need to create a DigestInfo structure for RSA signature with SHA-512
        byte[] hash = Base64.getDecoder().decode(hashInBase64);

        // Create DigestInfo for SHA-512
        // DER encoding: SEQUENCE { SEQUENCE { OID, NULL }, OCTET STRING }
        byte[] sha512Oid = {
                0x30, 0x51, // SEQUENCE (81 bytes)
                0x30, 0x0d, // SEQUENCE (13 bytes)
                0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x03, // OID 2.16.840.1.101.3.4.2.3
                                                                                         // (SHA-512)
                0x05, 0x00, // NULL
                0x04, 0x40 // OCTET STRING (64 bytes)
        };

        byte[] digestInfo = new byte[sha512Oid.length + hash.length];
        System.arraycopy(sha512Oid, 0, digestInfo, 0, sha512Oid.length);
        System.arraycopy(hash, 0, digestInfo, sha512Oid.length, hash.length);

        // Sign the DigestInfo using NONEwithRSA (raw RSA signature)
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initSign(privateKey);
        signature.update(digestInfo);
        return Base64.getEncoder().encodeToString(signature.sign());
    }
}
