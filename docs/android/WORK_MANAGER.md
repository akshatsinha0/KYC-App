# WorkManager Sync – App Configuration

What it does
- Periodic sync every 15 minutes with network + battery-not-low constraints
- Manual trigger for immediate sync
- Idempotent finalize using digest-derived Idempotency-Key
- Encrypted offline queue stored at app/files/queue/pending.enc

Key classes
- SyncStatusActivity: schedules periodic sync, shows state
- KycSyncWorker: reads encrypted queue, posts finalize with OkHttp
- KycSdk.enqueueFinalize: queues finalize payload using SHA-256 digest

Base URL
- BuildConfig.KYC_BASE_URL controls local server URL per build type
- Default (debug/release): http://10.0.2.2:8080 (Android emulator loopback)
- For physical devices: set a dev build with your PC’s LAN IP, e.g. http://192.168.1.50:8080

How to change base URL
- In android/app/build.gradle.kts update:
  buildConfigField("String","KYC_BASE_URL","\"http://192.168.1.50:8080\"")

Observability
- SyncStatusActivity observes unique work "kyc_sync" and displays state transitions
- Failures return Result.retry() and will backoff exponentially

Security
- Queue is AES-GCM encrypted with key in Android Keystore
- Only digests/minimal data are sent to local server

