plugins {
    id("workshop.java-convention")
}

dependencies {
    implementation(project(":producer"))
}

tasks.runMain {
    mainClass = "workshop.Consumer"
}
