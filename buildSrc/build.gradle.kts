plugins {
    `kotlin-dsl`
}

dependencies {
    api("org.springframework.boot:spring-boot-gradle-plugin:3.2.3")
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