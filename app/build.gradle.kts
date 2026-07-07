plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // PHASE 01C — DEPENDENCY INJECTION FOUNDATION
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.gym.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gym.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.6.0-phase01g"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// PHASE 01C — DEPENDENCY INJECTION FOUNDATION
// Required by Hilt so the generated Dagger components can resolve types
// that kapt itself hasn't fully stubbed yet during incremental compilation.
kapt {
    correctErrorTypes = true
}

dependencies {
    // Compose BOM keeps all Compose artifact versions aligned.
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    // Needed for collectAsStateWithLifecycle(), used by StartScreen (Phase 01C)
    // to collect StartViewModel's StateFlow safely. Same version as
    // lifecycle-runtime-ktx above to keep the lifecycle artifact group aligned.
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    // Navigation Foundation (Phase 01B). Pinned to a version from the
    // Kotlin 1.9.x / pre-K2-Compose-compiler era for maximum compatibility
    // with the current toolchain (Kotlin 1.9.24 + Compose Compiler 1.5.14).
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dependency Injection Foundation (Phase 01C).
    // Hilt 2.51.1: last widely-adopted Hilt release built around kapt,
    // matching the project's existing Kotlin 1.9.24 / kapt toolchain
    // (no K2/KSP migration required).
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Bridges Hilt-injected ViewModels (hiltViewModel()) into Navigation
    // Compose destinations. 1.2.0 is the version aligned with
    // navigation-compose 2.7.7 and the current Compose BOM.
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room Database Foundation (Phase 01E).
    // Room 2.6.1: last well-proven Room release built around kapt (not
    // KSP-only), matching the project's existing Kotlin 1.9.24 / kapt /
    // Hilt 2.51.1 toolchain — no unrelated upgrades required.
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // DataStore Foundation (Phase 01G).
    // Preferences DataStore only — for simple, app-level state that is not
    // per-user domain data (see AppStateDataStore). 1.1.1 is the current
    // stable release and has no dependency on Kotlin/AGP versions beyond
    // what the project already uses (Kotlin 1.9.24, AGP 8.5.2); it does not
    // require kapt/KSP, so it needs no annotation-processor wiring.
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    // Coroutines test support for Room DAO tests (runTest).
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // Room instrumented database tests (need a real/instrumented SQLite driver).
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
