plugins {
    `kotlin-dsl`
}

dependencies {
    api("org.springframework.boot:spring-boot-gradle-plugin:3.2.4")
}

gradlePlugin {
    plugins {
        create("build-timestamp") {
            id = "workshop.build-timestamp"
            implementationClass = "workshop.BuildTimestampPlugin"
        }
        create("java") {
            id = "workshop.java"
            implementationClass = "workshop.JavaPlugin"
        }
        create("spring-boot") {
            id = "workshop.spring-boot"
            implementationClass = "workshop.SpringBootExtendedPlugin"
        }
    }
}