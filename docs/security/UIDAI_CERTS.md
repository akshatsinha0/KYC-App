# UIDAI Certificates - Local Embedding Guide

Purpose
- Provide the UIDAI root and intermediate certificates (PEM) locally for strict Aadhaar Offline signature validation.

Where to place
- Android (app scope): android/aadhaar-offline/src/main/res/raw/uidai_chain.pem
  - Replace existing placeholder file uidai_cert_placeholder.pem

File format
- Concatenate PEM blocks, order: Root -> Intermediate(s)
- Example:

-----BEGIN CERTIFICATE-----
... root cert ...
-----END CERTIFICATE-----
-----BEGIN CERTIFICATE-----
... intermediate cert ...
-----END CERTIFICATE-----

App usage
- AadhaarOfflineProcessor.verifyAndDecrypt(zip, shareCode, context=appContext, config=AadhaarConfig.PRODUCTION)
  - When config.customTrustChain is null and context != null, the processor loads the embedded chain via UidaiTrustStore.loadEmbeddedChain(context).

Rotation
- Replace the PEM file and bump app version.
- Add a note to SECURITY_CHANGELOG.md with date and cert thumbprints.

Verification
- Add unit test with a signed sample (testing only) where signature validates against the embedded chain.


