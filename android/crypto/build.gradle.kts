plugins{ id("com.android.library"); id("org.jetbrains.kotlin.android") }
android{ namespace="com.loksakshya.kyc.crypto"; compileSdk=34; defaultConfig{ minSdk=23 } }
dependencies{ implementation("androidx.security:security-crypto:1.1.0-alpha06") }

