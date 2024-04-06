import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    workshop.`spring-convention`
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter")
    productionOnly(project(":producer"))
}
