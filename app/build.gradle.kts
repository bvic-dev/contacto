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
    alias(libs.plugins.kotlin.kover)
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
    // AndroidX de base + Navigation, Lifecycle, Activity
    implementation(libs.bundles.androidx.core)

    // Compose via BOM + bundle UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // DI (Hilt)
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Network
    implementation(libs.bundles.networking)

    // Room (runtime + ktx) + KSP compiler
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Pictures
    implementation(libs.bundles.coil)

    // Divers (datetime, lottie)
    implementation(libs.bundles.misc)

    // Debug / tooling
    debugImplementation(libs.bundles.compose.debug)

    // Unit Test (API Jupiter + MockK + Coroutines test + Truth)
    testImplementation(libs.bundles.test)
    // Junit 5 engine
    testRuntimeOnly(libs.junit.jupiter.engine)

    // Instrumentation tests
    androidTestImplementation(libs.bundles.android.test)
    // BOM Compose tests UI
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

// Lint avant la compilation
tasks.named("preBuild") { dependsOn("ktlintCheck") }
