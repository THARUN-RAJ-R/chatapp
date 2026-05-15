plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

kotlin {
    jvmToolchain(17)
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

        // ── Device testing: USB Debugging with adb reverse
        buildConfigField("String", "BASE_URL",    "\"https://chatapp-backend-1-shcw.onrender.com/\"")  
        buildConfigField("String", "WS_BASE_URL", "\"wss://chatapp-backend-1-shcw.onrender.com/ws\"")
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

    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

    // Firebase (keep messaging for push notifications; auth removed)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // DataStore (session storage)
    implementation(libs.datastore.prefs)

    // Coroutines
    implementation(libs.coroutines.android)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Gson
    implementation(libs.gson)
}
