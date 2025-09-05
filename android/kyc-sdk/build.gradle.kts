plugins{ id("com.android.library"); id("org.jetbrains.kotlin.android") }
android{ namespace="com.loksakshya.kyc.sdk"; compileSdk=34; defaultConfig{ minSdk=23 }; compileOptions{ sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }; kotlinOptions{ jvmTarget="17" } }
dependencies{
  implementation(project(":crypto"))
  implementation(project(":sync"))
  implementation(project(":analytics"))
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

