# Aadhaar Offline Module

This module provides secure verification and decryption of Aadhaar Offline XML files with strict UIDAI certificate trust chain validation.

## Features

- **ZIP Decryption**: Password-protected ZIP file extraction using share codes
- **PKCS#7 Signature Verification**: Cryptographic signature validation using BouncyCastle
- **UIDAI Trust Chain**: Embedded official UIDAI certificate chain for strict validation
- **Configurable Security**: Production, development, and testing configurations
- **XML Parsing**: Extract standard Aadhaar fields (name, UID, gender, DOB, etc.)

## Usage

### Basic Usage (Production Mode)

```kotlin
val result = AadhaarOfflineProcessor.verifyAndDecrypt(
    zipBytes = aadhaarZipFile,
    shareCode = "1234",
    context = applicationContext
)

if (result.ok) {
    println("Name: ${result.name}")
    println("UID: ${result.uid}")
    println("Gender: ${result.gender}")
    println("DOB: ${result.dob}")
} else {
    println("Verification failed: ${result.error}")
}
```

### Custom Configuration

```kotlin
val config = AadhaarConfig(
    strictTrustValidation = true,
    checkCertificateExpiry = true,
    customTrustChain = customPemChain,
    includeDetailedErrors = false
)

val result = AadhaarOfflineProcessor.verifyAndDecrypt(
    zipBytes = aadhaarZipFile,
    shareCode = "1234",
    context = applicationContext,
    config = config
)
```

### Development/Testing Mode

```kotlin
// Relaxed validation for development
val result = AadhaarOfflineProcessor.verifyAndDecrypt(
    zipBytes = aadhaarZipFile,
    shareCode = "1234",
    context = applicationContext,
    config = AadhaarConfig.DEVELOPMENT
)
```

## Security Configurations

### Production (Default)
- ✅ Strict trust chain validation
- ✅ Certificate expiry checking
- ❌ Detailed error messages (security)
- Uses embedded UIDAI certificates

### Development
- ❌ Relaxed trust validation
- ❌ Skip certificate expiry
- ✅ Detailed error messages
- Useful for testing with mock data

### Testing
- ✅ Strict trust validation
- ❌ Skip certificate expiry
- ✅ Detailed error messages
- Allows custom trust chains

## Trust Chain Management

The module includes embedded UIDAI certificates in `src/main/res/raw/uidai_cert_placeholder.pem`. These certificates are used to validate that Aadhaar XML signatures come from legitimate UIDAI sources.

### Certificate Updates

To update the trust chain:

1. Obtain the latest UIDAI certificate chain from official sources
2. Replace the content in `uidai_cert_placeholder.pem`
3. Ensure certificates are in PEM format
4. Test with `UidaiTrustStoreTest`

### Custom Trust Chains

You can provide custom certificate chains:

```kotlin
val customPem = """
-----BEGIN CERTIFICATE-----
[Your custom certificate content]
-----END CERTIFICATE-----
"""

val config = AadhaarConfig(customTrustChain = customPem)
```

## Error Codes

- `bad_zip`: Invalid or corrupted ZIP file
- `bad_share_code`: Incorrect share code for ZIP decryption
- `xml_missing`: No XML file found in ZIP
- `signature_missing`: No signature file found in ZIP
- `signature_invalid`: Signature verification failed or untrusted certificate
- `xml_parse_error`: XML parsing failed

## Dependencies

- **zip4j**: ZIP file handling with password support
- **BouncyCastle**: Cryptographic operations and PKCS#7 verification
- **Android Context**: For loading embedded certificate resources

## Testing

Run unit tests:
```bash
./gradlew :aadhaar-offline:test
```

Tests cover:
- Certificate loading and parsing
- Configuration validation
- Error handling scenarios
- Trust chain verification

## Security Notes

1. **Always use PRODUCTION config** in release builds
2. **Validate certificate expiry** in production
3. **Keep UIDAI certificates updated** as they may expire
4. **Never disable trust validation** in production
5. **Protect share codes** - they are sensitive user data

## Integration

This module is designed to work with:
- **KYC SDK**: For session management and result queuing
- **Sync Module**: For offline-first data synchronization
- **Crypto Module**: For additional encryption needs