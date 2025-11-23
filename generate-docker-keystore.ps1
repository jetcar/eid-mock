# PowerShell script to generate a self-signed SSL certificate for Docker with eid-mock domain
$keyStoreFile = "config/keystore_docker.p12"
$keyStorePassword = "changeit"
$keyAlias = "springboot"

$keytool = "keytool"

# Create config directory if it doesn't exist
New-Item -ItemType Directory -Force -Path "config" | Out-Null

& $keytool -genkeypair -alias $keyAlias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keyStoreFile -validity 365 -storepass $keyStorePassword -keypass $keyStorePassword -dname 'CN=eid-mock,OU=Dev,O=EID-Mock,L=City,ST=State,C=EE' -ext 'SAN=dns:eid-mock,dns:localhost,ip:127.0.0.1'
Write-Host "Keystore generated: $keyStoreFile"
Write-Host "Password: $keyStorePassword"
Write-Host "Domain: eid-mock (with SAN for localhost and 127.0.0.1)"
