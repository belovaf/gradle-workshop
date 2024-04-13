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
        create("spring-boot") {
            id = "workshop.spring-boot"
            implementationClass = "workshop.SpringBootExtendedPlugin"
        }
    }
}