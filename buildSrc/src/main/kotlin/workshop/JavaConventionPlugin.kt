package workshop

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

/**
 * Пример convention-плагина.
 * Конфигурирует настройки других плагинов, чтобы не дублировать логику.
 */
class JavaConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        apply(plugin = "workshop.java")
        apply(plugin = "maven-publish")

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

        tasks.withType<CompileTask> {
            compilerArgs.add("-parameters")
        }

        Unit
    }
}