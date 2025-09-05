plugins{ id("com.android.library"); id("org.jetbrains.kotlin.android") }
android{ namespace="com.loksakshya.kyc.sync"; compileSdk=34; defaultConfig{ minSdk=23 }; compileOptions{ sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }; kotlinOptions{ jvmTarget="17" } }
dependencies{
  implementation("androidx.work:work-runtime-ktx:2.9.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation(project(":crypto"))
}

