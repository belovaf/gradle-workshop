plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("java") {
            id = "workshop.java"
            implementationClass = "workshop.JavaPlugin"
        }
        create("java-convention") {
            id = "workshop.java-convention"
            implementationClass = "workshop.JavaConventionPlugin"
        }
    }
}