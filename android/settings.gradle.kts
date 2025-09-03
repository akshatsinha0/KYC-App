pluginManagement{
  repositories{ gradlePluginPortal(); google(); mavenCentral() }
  plugins{
    id("com.android.application") version "8.6.0"
    id("com.android.library") version "8.6.0"
    id("com.android.dynamic-feature") version "8.6.0"
    id("org.jetbrains.kotlin.android") version "1.9.24"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
  }
}

dependencyResolutionManagement{
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories{ google(); mavenCentral() }
}

plugins{}

rootProject.name="bharat-kyc-android"
include(":app",":kyc-sdk",":capture",":ocr",":face",":digilocker",":aadhaar-offline",":sync",":crypto",":i18n",":analytics",":parsers")

