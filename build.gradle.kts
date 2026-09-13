// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    configurations.configureEach {
        resolutionStrategy {
            force(
                "org.jdom:jdom2:2.0.6.1",
                "org.bitbucket.b_c:jose4j:0.9.7",
                "org.bouncycastle:bcpkix-jdk18on:1.84",
                "org.bouncycastle:bcprov-jdk18on:1.84",
                "org.bouncycastle:bcutil-jdk18on:1.84"
            )
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
