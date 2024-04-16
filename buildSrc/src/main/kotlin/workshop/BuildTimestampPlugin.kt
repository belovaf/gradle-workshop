package workshop

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.withType
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo

class BuildTimestampPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        plugins.withId("workshop.java") {
            val timestampService = registerBuildTimestampService()
            tasks.named<Jar>("jar") {
                usesService(timestampService)
                manifest {
                    attributes["Timestamp"] = timestampService.get().timestampString
                }
            }
        }
        plugins.withId("org.springframework.boot") {
            val timestampService = registerBuildTimestampService()
            tasks.withType<BuildInfo>().configureEach {
                properties {
                    time.set(timestampService.map { it.timestampString })
                }
            }
        }
    }

    private fun Project.registerBuildTimestampService() =
        gradle.sharedServices.registerIfAbsent("build-timestamp", BuildTimestampService::class)
}
