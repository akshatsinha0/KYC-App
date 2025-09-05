plugins{ id("com.android.dynamic-feature"); id("org.jetbrains.kotlin.android") }
android{ namespace="com.loksakshya.kyc.ocr"; compileSdk=34; defaultConfig{ minSdk=23 }; compileOptions{ sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }; kotlinOptions{ jvmTarget="17" } }
dependencies{ implementation(project(":app")); testImplementation("junit:junit:4.13.2") }

