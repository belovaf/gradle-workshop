package workshop

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import javax.inject.Inject

abstract class JavaPluginExtension {
    abstract val version: Property<JavaVersion>
    abstract val sourcesDir: DirectoryProperty

    @get:Inject
    protected abstract val project: Project

    fun withSourcesJar() = project.run {
        val sourcesJarTask = tasks.register<Jar>("sourcesJar") {
            group = BUILD_GROUP
            from(sourcesDir)
            archiveClassifier.set("sources")
            destinationDirectory.set(layout.buildDirectory.dir("libs"))
        }
        val sourceElements = configurations.consumable("sourceElements") {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
                attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            }
            outgoing.artifact(sourcesJarTask)
        }
        (project.components["java"] as AdhocComponentWithVariants).apply {
            addVariantsFromConfiguration(sourceElements.get()) {
                mapToMavenScope("runtime")
                mapToOptional()
            }
        }
    }

    init {
        version.convention(JavaVersion.current())
        sourcesDir.convention(project.layout.projectDirectory.dir("src/main/java"))
    }
}