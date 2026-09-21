import java.util.Base64

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingKeystoreBase64: String? = System.getenv("ANDROID_SIGNING_KEYSTORE_BASE64")
val signingKeyAlias = System.getenv("ANDROID_SIGNING_KEY_ALIAS") ?: "fhws"
val signingStorePassword = System.getenv("ANDROID_SIGNING_STORE_PASSWORD")
val signingKeyPassword = System.getenv("ANDROID_SIGNING_KEY_PASSWORD")

android {
    namespace = "com.fhws.transport"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fhws.transport"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (signingKeystoreBase64 != null && signingStorePassword != null && signingKeyPassword != null) {
            create("release") {
                storeFile = rootProject.file("keystore-release.jks").apply {
                    parentFile.mkdirs()
                    writeBytes(Base64.getDecoder().decode(signingKeystoreBase64))
                }
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")

    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Safe, modern WebView APIs (e.g. requestedWith, ForceDark).
    implementation("androidx.webkit:webkit:1.12.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}