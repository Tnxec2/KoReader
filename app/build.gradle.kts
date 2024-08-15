plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    id("kotlin-kapt")
}

android {
    namespace = "com.kontranik.koreader"
    compileSdk = 34
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "com.kontranik.koreader"
        minSdk = 26
        targetSdk = 34
        versionCode = 11
        versionName = "1.4.4"
        setProperty("archivesBaseName", "$applicationId-v$versionCode($versionName)")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
//                arguments += listOf("room.schemaLocation": "$projectDir/schemas".toString())
            }
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Paging
    implementation(libs.androidx.paging.paging.runtime.ktx)
    implementation(libs.androidx.room.room.paging)
    implementation(libs.androidx.paging.compose) // jetpack compose

    // Room components
    implementation(libs.androidx.room.room.runtime)
    implementation(libs.androidx.room.room.ktx)
    kapt(libs.androidx.room.room.compiler)
    androidTestImplementation(libs.androidx.room.room.testing)

    // Lifecycle components
    implementation( libs.androidx.lifecycle.viewmodel.ktx)
    implementation( libs.androidx.lifecycle.livedata.ktx)
    implementation( libs.androidx.lifecycle.common.java8)
    implementation( libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.compose.theme.adapter)
    implementation(libs.androidx.activity.compose)

    // Navigation
    // Jetpack Compose Integration
    implementation(libs.androidx.navigation.compose)
    // implementation(libs.androidx.hilt.navigation.compose)


    implementation("androidx.fragment:fragment-ktx:1.5.2") // use Compose with Fragments
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.material.material)
    implementation(libs.androidx.recyclerview)

    // For control over item selection of both touch and mouse driven selection
    implementation(libs.androidx.recyclerview.selection)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.gson)

    implementation("nl.siegmann.epublib:epublib-core:3.1") {
        exclude(group = "org.slf4j")
        exclude(group = "xmlpull")
    }
    implementation(libs.slf4j.android)

    implementation(libs.imagezoom)

    implementation(libs.jackson.databind)

    implementation(libs.jsoup)
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")

    implementation(libs.colorpicker)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.robolectric)
}

