package workshop

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import workshop.attributes.JavaVersionCompatibilityRule
import workshop.attributes.LibraryElementsCompatibilityRules
import workshop.attributes.TargetJvmEnvironmentDisambiguationRule
import workshop.attributes.attr
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class JavaPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory
) : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        apply<BasePlugin>()

        val timestampService = gradle.sharedServices.registerIfAbsent("timestamp", TimestampBuildService::class) {
            parameters.format.set("yyyy-MM-dd HH:mm:ss")
        }

        val ext = extensions.create<JavaPluginExtension>("java")

        dependencies {
            attributesSchema {
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE) {
                    compatibilityRules.add(JavaVersionCompatibilityRule::class)
                }
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE) {
                    compatibilityRules.add(LibraryElementsCompatibilityRules::class)
                }
                attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE) {
                    disambiguationRules.add(TargetJvmEnvironmentDisambiguationRule::class)
                }
            }
        }

        val api = configurations.dependencyScope("api")

        val implementation = configurations.dependencyScope("implementation") {
            extendsFrom(api.get())
        }

        val compileClasspath = configurations.resolvable("compileClasspath") {
            extendsFrom(implementation.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_API))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.CLASSES))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
        }

        val runtimeClasspath = configurations.resolvable("runtimeClasspath") {
            extendsFrom(implementation.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_RUNTIME))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.CLASSES))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
        }

        val compileTask = tasks.register<CompileTask>("compile") {
            this.sourcePath.set(ext.sourcesDir)
            this.classPath.from(compileClasspath)
            this.classesDir.set(layout.buildDirectory.dir("classes/java/main"))
            this.release.set(ext.version)
        }

        val jarTask = tasks.register<Jar>("jar") {
            from(compileTask.map { it.classesDir })
            destinationDirectory.set(layout.buildDirectory.dir("libs"))
            usesService(timestampService)
            manifest {
                attributes["Timestamp"] = timestampService.get().timestampString
            }
        }

        tasks.named("assemble") {
            dependsOn(jarTask)
        }

        tasks.register<JavaExec>("runMain") {
            classpath(compileTask.map { it.classesDir.asFile }, runtimeClasspath)
        }

        val apiElements = configurations.consumable("apiElements") {
            extendsFrom(api.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_API))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.JAR))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
            outgoing.artifact(jarTask)
        }

        val apiClasses = configurations.consumable("apiClasses") {
            extendsFrom(api.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_API))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.CLASSES))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
            outgoing.artifact(compileTask)
        }

        val runtimeElements = configurations.consumable("runtimeElements") {
            extendsFrom(implementation.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_RUNTIME))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.JAR))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
            outgoing.artifact(jarTask)
        }

        val runtimeClasses = configurations.consumable("runtimeClasses") {
            extendsFrom(implementation.get())
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, attr(Category.LIBRARY))
                attribute(Usage.USAGE_ATTRIBUTE, attr(Usage.JAVA_RUNTIME))
                attribute(Bundling.BUNDLING_ATTRIBUTE, attr(Bundling.EXTERNAL))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attr(LibraryElements.CLASSES))
                attributeProvider(
                    TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                    ext.version.map { it.majorVersion.toInt() },
                )
            }
            outgoing.artifact(compileTask)
        }

        val javaComponent = softwareComponentFactory.adhoc("java").apply {
            addVariantsFromConfiguration(apiElements.get()) { mapToMavenScope("compile") }
            addVariantsFromConfiguration(runtimeElements.get()) { mapToMavenScope("runtime") }
            addVariantsFromConfiguration(apiClasses.get()) { skip() }
            addVariantsFromConfiguration(runtimeClasses.get()) { skip() }
        }
        components.add(javaComponent)
        Unit
    }
}
