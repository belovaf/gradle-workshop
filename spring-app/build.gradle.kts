import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    workshop.`spring-boot`
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter")
    productionOnly(project(":producer"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

springBoot {
    buildInfo()
}

normalization {
    runtimeClasspath {
        ignore("META-INF/build-info.properties")
    }
}