plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

val releaseKeystorePath = System.getenv("ORPHEUS_RELEASE_KEYSTORE_PATH")
val releaseKeystorePassword = System.getenv("ORPHEUS_RELEASE_KEYSTORE_PASSWORD")
val releaseKeyAlias = System.getenv("ORPHEUS_RELEASE_KEY_ALIAS")
val releaseKeyPassword = System.getenv("ORPHEUS_RELEASE_KEY_PASSWORD")
val releaseSigningConfigured = listOf(
    releaseKeystorePath,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.blazares.orpheus"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.blazares.orpheus"
        minSdk = 26
        targetSdk = 37
        versionCode = 4
        versionName = "2026.08.31.1447"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (releaseSigningConfigured) {
                storeFile = file(requireNotNull(releaseKeystorePath))
                storePassword = requireNotNull(releaseKeystorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val jacocoTestReport = tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("testDebugUnitTest"))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/theme/*", "**/MainActivity*",
        "**/AudioCaptureProvider*", "**/ComposableSingletons*",
        "**/ui/components/*", "**/ui/screen/*", "**/ui/notebuilder/NoteBuilderScreen*",
        "**/ui/OrpheusSplashScreen*",
        "**/ui/notebuilder/NoteBuilderPalette*", "**/notebuilder/audio/*"
    )
    val debugTree = files(
        tasks.named("compileDebugKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).flatMap { it.destinationDirectory }
    ).asFileTree.matching {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

val jacocoTestCoverageVerification = tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(jacocoTestReport)
    sourceDirectories.setFrom(jacocoTestReport.map { it.sourceDirectories })
    classDirectories.setFrom(jacocoTestReport.map { it.classDirectories })
    executionData.setFrom(jacocoTestReport.map { it.executionData })
    violationRules {
        rule {
            limit {
                minimum = "0.95".toBigDecimal() // 95% (100% is blocked by Kotlin generated code)
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
