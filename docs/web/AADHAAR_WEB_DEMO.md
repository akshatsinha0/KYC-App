# Aadhaar Web Demo – Local Verification

What it does
- Decrypts password-protected Aadhaar Offline ZIP in-browser using zip.js
- Extracts XML and shows key fields (name, uid, gender, dob, referenceId)
- Detects presence of signature file (.p7b/.p7s)
- Signature verification is performed in Android app (on-device PKCS#7 with UIDAI trust)

URL
- http://localhost:8080/web/aadhaar.html

Test data
- Use a local sample Aadhaar ZIP and share code (e.g. 1234)
- No data is uploaded; processing happens in your browser

Notes
- If you need browser-side PKCS#7 verification, integrate PKIjs and embed UIDAI chain; keep size in check
- For production, verification should remain on-device as implemented in AadhaarOfflineProcessor

