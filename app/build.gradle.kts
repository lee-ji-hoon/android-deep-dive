import com.sample.playground.convention.ProjectConfigurations

plugins {
    id("playground.android.application.compose")
    id("playground.android.application")
    id("playground.android.hilt")
}

android {
    namespace = "com.sample.playground"

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
    implementation(project(":feature:window"))
    implementation(project(":feature:compose-layout"))
    implementation(project(":feature:inset-animation"))
    implementation(project(":base"))

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

}