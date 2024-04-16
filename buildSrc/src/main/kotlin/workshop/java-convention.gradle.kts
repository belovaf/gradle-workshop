package workshop

plugins {
    `maven-publish`
}

apply(plugin = "workshop.java")
apply(plugin = "workshop.build-timestamp")

extensions.configure<JavaPluginExtension> {
    withSourcesJar()
    version.set(JavaVersion.VERSION_17)
}

tasks.withType<CompileTask>().configureEach {
    compilerArgs.add("-parameters")
}

publishing {
    publications {
        create<MavenPublication>("java") {
            from(components["java"])
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
        }
        repositories {
            maven(rootProject.layout.projectDirectory.dir(".repository").asFile) {
                name = "workshopRepo"
            }
        }
    }
}
