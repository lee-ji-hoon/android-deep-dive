plugins {
    id("playground.android.application.compose")
    id("playground.android.application")
    id("playground.android.hilt")
}

android {
    namespace = "com.sample.feature.window"
}

dependencies {
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
}