import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.sample.android_playground.convention.configureAndroidCompose
import com.sample.android_playground.convention.findPluginId
import com.sample.android_playground.convention.libs

@Suppress("UNUSED")
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.findPluginId("android.library"))
            extensions.configure<LibraryExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}
