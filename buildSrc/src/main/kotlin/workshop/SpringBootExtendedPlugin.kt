package workshop

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

class SpringBootExtendedPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        apply(plugin = "org.springframework.boot")

        plugins.withId("java") {
            val productionOnly = configurations.dependencyScope("productionOnly")
            val productionOnlyPath = configurations.resolvable("productionOnlyPath") {
                extendsFrom(productionOnly.get())
                attributes {
                    val sourceAttributes =
                        configurations.named(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME).get().attributes

                    sourceAttributes.keySet().forEach {
                        @Suppress("UNCHECKED_CAST")
                        attributeProvider(it as Attribute<Any>, provider { sourceAttributes.getAttribute(it) })
                    }
                }
            }
            configurations.named(SpringBootPlugin.PRODUCTION_RUNTIME_CLASSPATH_CONFIGURATION_NAME) {
                extendsFrom(productionOnly.get())
            }

            tasks.named<BootJar>(SpringBootPlugin.BOOT_JAR_TASK_NAME) {
                classpath(productionOnlyPath)
            }
        }

        Unit
    }
}