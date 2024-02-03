plugins {
    id("playground.android.library")
    id("playground.android.library.compose")
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