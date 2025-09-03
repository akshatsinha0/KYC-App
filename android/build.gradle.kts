// Root build script: centralize plugin declarations
plugins{
  id("com.android.application") apply false
  id("com.android.library") apply false
  id("com.android.dynamic-feature") apply false
  id("org.jetbrains.kotlin.android") apply false
  id("org.jetbrains.kotlin.jvm") apply false
}

// Repositories are configured in settings.gradle.kts

