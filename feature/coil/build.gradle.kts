plugins {
    id("playground.android.library")
    id("playground.android.library.compose")
    id("playground.android.hilt")
}

android {
    namespace = "com.sample.feature.coil"
}

dependencies {
    implementation(project(":base"))
    implementation(libs.bundles.coil3)
    implementation(libs.bundles.glide)
}