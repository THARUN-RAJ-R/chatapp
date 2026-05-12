plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace  = "com.chatapp.android"
    compileSdk = 35

    defaultConfig {
        applicationId   = "com.chatapp.android"
        minSdk          = 26
        targetSdk       = 35
        versionCode     = 1
        versionName     = "1.0"

        // ── Device testing: PC WiFi IP (both phone and PC on same network)
        buildConfigField("String", "BASE_URL",    "\"http://10.89.95.74:8080/\"")  // real device → PC
        buildConfigField("String", "WS_BASE_URL", "\"ws://10.89.95.74:8080/ws\"")
        // ── Emulator testing (uncomment if using AVD)
        // buildConfigField("String", "BASE_URL",    "\"http://10.0.2.2:8080/\"")
        // buildConfigField("String", "WS_BASE_URL", "\"ws://10.0.2.2:8080/ws\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // For release: replace with your deployed server IP/domain
            // buildConfigField("String", "BASE_URL",    "\"https://yourserver.com/\"")
            // buildConfigField("String", "WS_BASE_URL", "\"wss://yourserver.com/ws\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose     = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.okhttp)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)

    // DataStore
    implementation(libs.datastore.prefs)

    // Encrypted Prefs (JWT token storage)
    implementation(libs.encrypted.prefs)

    // Coroutines
    implementation(libs.coroutines.android)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Gson
    implementation(libs.gson)
}
