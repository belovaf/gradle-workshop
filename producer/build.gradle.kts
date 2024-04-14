plugins {
    workshop.`java-convention`
}

dependencies {
    api("com.google.guava:guava:33.1.0-jre")
}

tasks.runMain {
    mainClass = "workshop.Producer"
}
