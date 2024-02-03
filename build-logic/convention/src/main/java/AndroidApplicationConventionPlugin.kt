import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.sample.android_playground.convention.ProjectConfigurations
import com.sample.android_playground.convention.configureKotlinAndroid
import com.sample.android_playground.convention.findPluginId
import com.sample.android_playground.convention.libs

@Suppress("UNUSED")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPluginId("android.application"))
                apply(libs.findPluginId("kotlin.android"))
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = ProjectConfigurations.targetSdk
            }
        }
    }
}