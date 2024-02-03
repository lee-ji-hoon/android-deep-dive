import com.sample.android_playground.convention.ProjectConfigurations

plugins {
    id("android_playground.android.application.compose")
    id("android_playground.android.application")
    id("android_playground.android.hilt")
}

android {
    namespace = "com.sample.android_playground"

    defaultConfig {
        applicationId = ProjectConfigurations.applicationId
        versionCode = ProjectConfigurations.versionCode
        versionName = ProjectConfigurations.versionName
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

}