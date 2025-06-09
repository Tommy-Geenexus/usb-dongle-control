import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.compiler.report.generator)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
}

android {
    namespace = "io.github.tommygeenexus.usbdonglecontrol"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.tommygeenexus.usbdonglecontrol"
        minSdk = 31
        targetSdk = 35
        versionCode = 7
        versionName = "3.1.0"
    }

    signingConfigs {
        create("release") {
            val keyStorePassword = "KS_PASSWORD"
            val keyStoreKeyAlias = "KS_KEY_ALIAS"
            val properties = Properties().apply {
                val file = File(projectDir.parent, "keystore.properties")
                if (file.exists()) {
                    load(FileInputStream(file))
                }
            }
            val password = properties
                .getOrDefault(keyStorePassword, null)
                ?.toString()
                ?: System.getenv(keyStorePassword)
            val alias = properties
                .getOrDefault(keyStoreKeyAlias, null)
                ?.toString()
                ?: System.getenv(keyStoreKeyAlias)
            storeFile = File(projectDir.parent, "keystore.jks")
            storePassword = password
            keyAlias = alias
            keyPassword = password
            enableV1Signing = false
            enableV2Signing = false
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { keyWord ->
            version.uppercase().contains(keyWord)
        }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

detekt {
    baseline = file("$projectDir/config/detekt/baseline.xml")
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
        )
    }
}

room {
    schemaDirectory("$projectDir/schemas/")
}

spotless {
    kotlin {
        ratchetFrom("origin/main")
        target("**/*.kt")
        licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
    }
}

dependencies {
    debugImplementation(libs.leakcanary)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.implementation)
    ksp(libs.bundles.ksp)
    lintChecks(libs.bundles.lint.checks)
}
