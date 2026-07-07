// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false

    // PHASE 01C — DEPENDENCY INJECTION FOUNDATION
    // kapt is required for the Hilt annotation processor (hilt-android-compiler).
    // Pinned to the same Kotlin version as org.jetbrains.kotlin.android above,
    // since Kotlin Gradle plugins must share a single version across a build.
    id("org.jetbrains.kotlin.kapt") version "1.9.24" apply false

    // Hilt Gradle plugin. 2.51.1 is the last Hilt line with broad, well-proven
    // compatibility with Kotlin 1.9.x (kapt, not K2/KSP-only) and AGP 8.5.x,
    // matching the rest of the current toolchain without upgrading anything.
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}
