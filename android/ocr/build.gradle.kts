plugins{ id("com.android.dynamic-feature"); id("org.jetbrains.kotlin.android") }
android{ namespace="com.loksakshya.kyc.ocr"; compileSdk=34; defaultConfig{ minSdk=23 } }
dependencies{ implementation(project(":app")) }

