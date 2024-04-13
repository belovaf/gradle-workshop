package workshop

plugins {
    `maven-publish`
}

apply(plugin = "workshop.java")

extensions.configure<JavaPluginExtension> {
    withSourcesJar()
    version.set(JavaVersion.VERSION_17)
}

tasks.withType<CompileTask> {
    compilerArgs.add("-parameters")
}

publishing {
    publications {
        create<MavenPublication>("java") {
            from(components["java"])
            versionMapping {
                usage(Usage.JAVA_RUNTIME) {
                    fromResolutionResult()
                }
            }
        }
        repositories {
            maven(rootProject.layout.projectDirectory.dir(".repository").asFile.absoluteFile) {
                name = "workshop-repo"
            }
        }
    }
}
