plugins{ id("com.android.application"); id("org.jetbrains.kotlin.android") }

android{
  namespace="com.loksakshya.bharatkyc"
  compileSdk=34
  defaultConfig{
    applicationId="com.loksakshya.bharatkyc"
    minSdk=23
    targetSdk=34
    versionCode=1
    versionName="0.1.0"
    buildConfigField("String","KYC_BASE_URL","\"http://10.0.2.2:8080\"")
  }
  buildTypes{
    release{ isMinifyEnabled=false; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro") }
    debug{ isMinifyEnabled=false }
  }
  buildFeatures{ viewBinding=true; buildConfig=true }
  dynamicFeatures+=setOf(":ocr",":face")
  packaging{ resources{ excludes+=setOf("META-INF/*") } }
}

dependencies{
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.activity:activity-ktx:1.9.2")
  implementation("androidx.work:work-runtime-ktx:2.9.0")

  implementation(project(":kyc-sdk"))
  implementation(project(":digilocker"))
  implementation(project(":aadhaar-offline"))
  implementation(project(":capture"))
  implementation(project(":sync"))
  implementation(project(":crypto"))
  implementation(project(":i18n"))
  implementation(project(":analytics"))
}

