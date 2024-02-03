package com.sample.android_playground.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = ProjectConfigurations.compileSdk

        defaultConfig {
            minSdk = ProjectConfigurations.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
            resourceConfigurations.addAll(listOf("en", "ko"))
        }

        compileOptions {
            sourceCompatibility = ProjectConfigurations.JAVA_VERSION
            targetCompatibility = ProjectConfigurations.JAVA_VERSION
        }
    }

    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = ProjectConfigurations.JAVA_VERSION.toString()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }
}