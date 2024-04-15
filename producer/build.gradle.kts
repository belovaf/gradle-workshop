plugins {
    workshop.`java-convention`
}

dependencies {
    api("org.apache.commons:commons-text:1.11.0")
    api("com.google.guava:guava:33.1.0-jre")
}

tasks.runMain {
    mainClass = "workshop.Producer"
}
