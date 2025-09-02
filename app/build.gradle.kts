import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mannodermaus.junit5)
}

android {
    signingConfigs {
        create("bvic") {
            storeFile =
                file(gradleLocalProperties(rootDir, providers).getProperty("STORE_FILE_PATH"))
            storePassword = gradleLocalProperties(rootDir, providers).getProperty("STORE_PASSWORD")
            keyPassword = gradleLocalProperties(rootDir, providers).getProperty("STORE_PASSWORD")
            keyAlias = gradleLocalProperties(rootDir, providers).getProperty("STORE_KEY")
        }
    }

    namespace = "com.bvic.contacto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bvic.contacto"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("bvic")
        }

        debug {
            versionNameSuffix = ".debug"
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
        freeCompilerArgs = listOf("-XXLanguage:+WhenGuards")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.ui.animation)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlin.date)
    implementation(libs.lottie)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.google.truth)
    androidTestImplementation(libs.junit.jupiter.api)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}

ktlint {
    version.set(libs.versions.ktlint)
    android.set(true)
    outputColorName.set("RED")
}

hilt {
    enableAggregatingTask = false
}

tasks.named("preBuild") { dependsOn("ktlintCheck") }
