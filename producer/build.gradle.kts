plugins {
    id("workshop.java-convention")
}

dependencies {
    api("com.google.guava:guava:33.0.0-jre")
}

tasks.runMain {
    mainClass = "workshop.Consumer"
}
