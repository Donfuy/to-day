plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    compileSdkVersion(32)

    defaultConfig {
        applicationId = "com.donfuy.android.today"
        minSdk = 21
        targetSdk = 32
        versionCode = 2
        versionName = "0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-rc01"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.donfuy.android.today"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    // Compose
    implementation(libs.compose)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    androidTestImplementation(libs.compose.junit4)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.compose.navigation)

    // Material 3
    implementation(libs.material3)
    implementation(libs.material.icons.extended)

    // Preferences DataStore
    implementation(libs.datastore.preferences)

    // Lifecycle
    implementation (libs.lifecycle.runtime.ktx)
    implementation (libs.lifecycle.livedata.ktx)
    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.lifecycle.viewmodel.compose)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    androidTestImplementation(libs.work.testing)

    // Accompanist SystemUiController
    implementation(libs.accompanist.systemuicontroller)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)

    // Jetpack Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
}

kapt {
    correctErrorTypes = true
}