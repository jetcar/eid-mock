# PowerShell script to generate a mock CA certificate in PKCS12 keystore
$outputP12 = "config/smartid_mock.p12"
$tempCert = "config/temp_ca.crt"
$keystorePassword = "changeit"
$keyAlias = "smartid-mock-ca-key"
$certAlias = "smartid-mock-ca-cert"

# Use keytool to generate CA certificate with private key in PKCS12 keystore
$keytool = "keytool"

Write-Host "Generating Smart-ID Mock CA keystore..."

# Generate keypair and certificate in PKCS12 keystore with key alias
& $keytool -genkeypair -alias $keyAlias -keyalg RSA -keysize 2048 -storetype PKCS12 `
    -keystore $outputP12 -validity 3650 `
    -storepass $keystorePassword -keypass $keystorePassword `
    -dname "CN=Smart-ID Mock CA, O=Mock, C=EE" `
    -ext "bc:c"

# Export the certificate
& $keytool -exportcert -alias $keyAlias -keystore $outputP12 `
    -storepass $keystorePassword -file $tempCert -rfc

# Import the certificate again with a different alias (as trusted cert entry)
& $keytool -importcert -alias $certAlias -keystore $outputP12 `
    -storepass $keystorePassword -file $tempCert -noprompt

# Clean up temporary certificate file
Remove-Item $tempCert -ErrorAction SilentlyContinue

Write-Host "Mock CA keystore generated: $outputP12"
Write-Host "Password: $keystorePassword"
Write-Host "Key alias: $keyAlias (contains private key + certificate)"
Write-Host "Certificate alias: $certAlias (trusted certificate only)"
Write-Host ""
Write-Host "Update application.yml with:"
Write-Host "  CA_KEYSTORE_PASSWORD=$keystorePassword"
Write-Host "  CA_KEY_ALIAS=$keyAlias"
Write-Host "  CA_CERT_ALIAS=$certAlias"

