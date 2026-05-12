plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

android {
    namespace = "com.edwardflores.magnetar.orpheus"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.edwardflores.magnetar.orpheus"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2026.05.12.1554"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

val jacocoTestReport by tasks.registering(JacocoReport::class) {
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

val jacocoTestCoverageVerification by tasks.registering(JacocoCoverageVerification::class) {
    dependsOn(jacocoTestReport)
    sourceDirectories.setFrom(jacocoTestReport.get().sourceDirectories)
    classDirectories.setFrom(jacocoTestReport.get().classDirectories)
    executionData.setFrom(jacocoTestReport.get().executionData)
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
