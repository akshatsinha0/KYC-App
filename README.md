# Bharat KYC

## Project Configuration

```json
{
  "name": "bharat-kyc",
  "org": "LokSakshya",
  "environments": ["dev","staging","prod"],
  "appId": "com.loksakshya.bharatkyc",
  "languages": ["en","hi"],
  "summary": "Lightweight, offline-first KYC app and SDK. All PII stays on-device. Local server only for DigiLocker callback and audits.",
  "modules": ["app","kyc-sdk","capture","ocr (dynamic)","face (dynamic)","digilocker","aadhaar-offline","sync","crypto","i18n","analytics"],
  "launch_flags": {"digilocker_enabled": false},
  "server": {
    "desc": "Local stub for DigiLocker callback and analytics",
    "start": "cd server && npm install && npm start",
    "urls": ["http://localhost:8080/healthz","http://localhost:8080/web/digilocker.html"]
  },
  "openapi": "openapi/openapi.yaml",
  "notes": [
    "No .md or .txt docs used. JSON/YAML only.",
    "ML models are placeholders. Keep size small.",
    "Replace org if needed; update appId accordingly.",
    "Base APK ≤ 15MB; OCR/Face in dynamic features",
    "Retention: 30 days"
  ]
}
```

A small, offline‑first KYC app + SDK for Android with a local server, simple web demos, and CLI tools.

What you get:
- Android app and SDK modules (Aadhaar offline ZIP, OCR, face liveness/match, sync, crypto)
- Local server with KYC session lifecycle and demo endpoints
- Browser demos (assistant, face liveness, Aadhaar ZIP, queue tester, DigiLocker stub)
- CLI tools to try features without a browser

Repo: https://github.com/akshatsinha0/KYC-App.git

## Requirements
- Windows, macOS, or Linux
- Java 17 (Temurin recommended)
- Node.js (latest LTS is fine) and npm
- Android SDK (Android Studio or command line; emulator optional)

Check versions:
```
java -version
node -v
npm -v
```

## Quick start (Windows PowerShell paths shown)
- Project root: `E:\KYC App\bharat-kyc`

Start the local server:
```
node E:\KYC App\bharat-kyc\server\src\server.js
```
Health check (new terminal):
```
Invoke-RestMethod http://localhost:8080/healthz
```

Open browser demos after server starts:
- Assistant: http://localhost:8080/web/assistant.html
- Face liveness: http://localhost:8080/web/face.html
- Aadhaar ZIP: http://localhost:8080/web/aadhaar.html
- Queue tester: http://localhost:8080/web/queue.html
- DigiLocker stub: http://localhost:8080/web/digilocker.html

## Build the Android app
From PowerShell:
```
E:\KYC App\bharat-kyc\android\gradlew.bat -p E:\KYC App\bharat-kyc\android --no-daemon :app:assembleDebug
```
APK output:
```
E:\KYC App\bharat-kyc\android\app\build\outputs\apk\debug\app-debug.apk
```
Notes:
- Emulator uses http://10.0.2.2:8080 to reach your PC server
- App uses encrypted local queue and retries when network is back

## CLI demos
Run without a browser (PowerShell):

Assistant explanation:
```
node E:\KYC App\bharat-kyc\server\src\cli\assistant.js aadhaar share_code "network issue"
```

Face liveness (sequence of eye openness values):
```
node E:\KYC App\bharat-kyc\server\src\cli\face_liveness.js 0.3,0.1,0.31
```

Aadhaar ZIP verify (example):
```
node E:\KYC App\bharat-kyc\server\src\cli\verify_aadhaar.js E:\KYC App\bharat-kyc\server\src\web\mock_aadhaar.zip 1234
```

## API quick test (PowerShell)
Create + finalize a KYC session and read it back:
```
$base='http://localhost:8080'
$idem=[guid]::NewGuid().ToString('N').Substring(0,16)
$create=Invoke-RestMethod -Uri "$base/v1/kyc/sessions" -Method Post -Headers @{ 'Idempotency-Key'=$idem } -Body (@{ method='document_scan' }|ConvertTo-Json) -ContentType 'application/json'
$sid=$create.id
$payload=@{foo='bar';ts=[DateTimeOffset]::Now.ToUnixTimeSeconds()}|ConvertTo-Json
$digestBytes=[System.Security.Cryptography.SHA256]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes($payload))
$hex=([System.BitConverter]::ToString($digestBytes)).Replace('-','').ToLower()
$fin=Invoke-RestMethod -Uri "$base/v1/kyc/sessions/$sid/finalize" -Method Post -Headers @{ 'Idempotency-Key'=$hex.Substring(0,32) } -Body (@{ resultDigest=$hex; signature=$hex; deviceId='pwsh' }|ConvertTo-Json) -ContentType 'application/json'
$get=Invoke-RestMethod -Uri "$base/v1/kyc/sessions/$sid" -Method Get
@{ create=$create; finalize=$fin; get=$get } | ConvertTo-Json -Depth 5
```
Expected: create -> finalized with the same id and a resultDigest.

## Run unit tests
Run safer tests first:
```
E:\KYC App\bharat-kyc\android\gradlew.bat -p E:\KYC App\bharat-kyc\android --no-daemon :aadhaar-offline:testDebugUnitTest :parsers:test
```
If OCR tests need more memory, try:
```
E:\KYC App\bharat-kyc\android\gradlew.bat -p E:\KYC App\bharat-kyc\android --no-daemon :ocr:testDebugUnitTest
```
Tip: increase system page file or set Gradle test JVM args if you see OutOfMemory.

## What’s inside (short)
- Android: app, kyc-sdk, aadhaar-offline (ZIP decrypt + signature verify), ocr, face (liveness + match), crypto (AES-GCM with Android Keystore), sync (WorkManager), digilocker, parsers
- Server: KYC session lifecycle, DigiLocker callback, assistant and face endpoints, analytics events
- Web: simple pages to try flows locally
- CLI: assistant, face liveness, aadhaar verify

## Notes
- Data stays local; server is for demo
- Offline-first: queues events and retries safely
- Idempotent finalize: safe to retry without duplicates
- Keep Node server running while testing app and web pages

