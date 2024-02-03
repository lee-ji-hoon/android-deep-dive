import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.sample.android_playground.convention.findPluginId
import com.sample.android_playground.convention.libs

@Suppress("UNUSED")
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPluginId("ksp"))
                apply(libs.findPluginId("hilt"))
            }

            dependencies {
                add("implementation", libs.findLibrary("dagger-hilt-android").get())
                add("ksp", libs.findLibrary("dagger-hilt-android-compiler").get())
            }
        }
    }
}