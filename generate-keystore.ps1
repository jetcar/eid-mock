# PowerShell script to generate a self-signed SSL certificate for Spring Boot
$keyStoreFile = "config/keystore.p12"
$keyStorePassword = ""
$keyAlias = "springboot"

$keytool = "keytool"

# Create config directory if it doesn't exist
New-Item -ItemType Directory -Force -Path "config" | Out-Null

& $keytool -genkeypair -alias $keyAlias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keyStoreFile -validity 365 -storepass $keyStorePassword -dname "CN=localhost, OU=Dev, O=SmartID Mock, L=City, S=State, C=EE"
Write-Host "Keystore generated: $keyStoreFile"
Write-Host "Password: $keyStorePassword"

