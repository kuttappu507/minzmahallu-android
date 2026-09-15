import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Fixed release keystore (committed for community sideload updates) — every
// build, local or CI, signs with the SAME key so updates install in-place
// over the previous version. Store/key passwords are not secrets here: the
// threat model is a sideloaded community app, not Play Store publishing.
val releaseSigning = Properties().apply {
    val f = rootProject.file("keystore/signing.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.mms.minzmahallu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mms.minzmahallu"
        minSdk = 26
        targetSdk = 35
        versionCode = 205
        versionName = "2.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        // Ensure SQLite / pdfbox native libs packaged correctly
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
    }

    signingConfigs {
        create("release") {
            if (releaseSigning.isNotEmpty()) {
                storeFile = rootProject.file(releaseSigning["storeFile"] as String)
                storePassword = releaseSigning["storePassword"] as String
                keyAlias = releaseSigning["keyAlias"] as String
                keyPassword = releaseSigning["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            // Keep minify OFF until launch is proven stable – then re-enable with proper keeps
            // The previous isMinifyEnabled=true with an incomplete proguard file stripped Compose/SQLite classes
            // and was the likely cause of "installs but fails to open" on release builds.
            isMinifyEnabled = false
            isShrinkResources = false
            // Fixed committed keystore → in-place updates between releases
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            // shrinkResources also false for debug – faster builds
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Required for java.time desugaring if Format ever uses it again, and for other libs
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        resources.excludes += "/META-INF/DEPENDENCIES"
        jniLibs.useLegacyPackaging = false
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    // Desugaring for java.time / nio on minSdk 26 (safe to include)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.6")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // SQLite (bundled)
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // DataStore for prefs
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Charts (lightweight pure compose alternative - we draw custom)
    // PDF
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // QR
    implementation("com.google.zxing:core:3.5.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
