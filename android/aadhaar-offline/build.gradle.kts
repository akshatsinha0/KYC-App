plugins{ id("com.android.library"); id("org.jetbrains.kotlin.android") }
android{
  namespace="com.loksakshya.kyc.aadhaar"; compileSdk=34; defaultConfig{ minSdk=23 }
  compileOptions{ sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }
  kotlinOptions{ jvmTarget="17" }
}
dependencies{
  implementation("net.lingala.zip4j:zip4j:2.11.5")
  implementation("org.bouncycastle:bcprov-jdk15to18:1.78.1")
  implementation("org.bouncycastle:bcpkix-jdk15to18:1.78.1")
  testImplementation("junit:junit:4.13.2")
}

