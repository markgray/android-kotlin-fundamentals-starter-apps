import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.android.devbyteviewer"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    buildFeatures {
        dataBinding = true
    }
    namespace = "com.example.android.devbyteviewer"

}

dependencies {
    implementation("androidx.activity:activity-ktx:1.12.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21")

    // support libraries
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.material:material:1.13.0")

    // Android KTX
    implementation("androidx.core:core-ktx:1.17.0")

    // constraint layout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // navigation
    implementation("androidx.navigation:navigation-runtime-ktx:2.9.6")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")

    // coroutines for getting off the UI thread
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")

    // moshi for parsing the JSON format
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")

    // joda time library for dealing with time
    implementation("joda-time:joda-time:2.14.0")

    // arch components
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // glide for images
    implementation("com.github.bumptech.glide:glide:5.0.5")
    ksp("com.github.bumptech.glide:compiler:5.0.5")

    // Room dependency
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // Work dependency
    implementation("androidx.work:work-runtime-ktx:2.11.0")
}
