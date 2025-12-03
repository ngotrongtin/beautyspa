plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    // Apply Google Services plugin to process google-services.json
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.beautyspa.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.beautyspa.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Expose API base URL to BuildConfig. Override with -PAPI_BASE_URL or gradle.properties if needed.
        val apiBaseUrl = (project.findProperty("API_BASE_URL") as String?) ?: "http://10.235.90.91:4000"
        buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl}\"")
        // Stripe publishable key provided via -PSTRIPE_PUBLISHABLE_KEY or gradle/local properties
        val stripeKey = (project.findProperty("STRIPE_PUBLISHABLE_KEY") as String?) ?: "pk_test_51NnANvICFXSh1wtRd0oyTnWdPyPnv5RFYePJktzqAVwff8LpMAUr1XOfXV8cIM3Uoxi5IsbIUUAJw1YVMYTFpUov006DzGLZ6A"
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"${stripeKey}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose BOM (Bill of Materials)
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Coil for image loading (Compose compatible)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // Material Components (BottomNavigationView, CardView / MaterialCardView)
    implementation("com.google.android.material:material:1.10.0")

    // Firebase BoM and Realtime Database
//    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
//    implementation("com.google.firebase:firebase-analytics-ktx")
//    implementation("com.google.firebase:firebase-database-ktx")
//    implementation("androidx.compose.foundation:foundation:1.9.5")

    // Networking for chatbot API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")

    // Stripe Android SDK for payment confirmation
    implementation("com.stripe:stripe-android:20.45.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.11.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}