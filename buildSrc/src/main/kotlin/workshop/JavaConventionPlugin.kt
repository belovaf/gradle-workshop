package workshop

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

/**
 * Пример convention-плагина.
 * Конфигурирует настройки других плагинов, чтобы не дублировать логику.
 */
class JavaConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        apply<JavaPlugin>()
        apply<MavenPublishPlugin>()

        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            version.set(JavaVersion.VERSION_17)
        }

        extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>("java") {
                    from(components["java"])
                    versionMapping {
                        usage(Usage.JAVA_RUNTIME) {
                            fromResolutionResult()
                        }
                    }
                }
            }
        }
    }
}