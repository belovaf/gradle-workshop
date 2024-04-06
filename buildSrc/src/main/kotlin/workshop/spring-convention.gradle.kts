package workshop

plugins {
    java
    id("org.springframework.boot")
}

val productionOnly = configurations.dependencyScope("productionOnly")
val productionOnlyPath = configurations.resolvable("productionOnlyPath") {
    extendsFrom(productionOnly.get())
    attributes {
        val sourceAttributes = configurations.runtimeClasspath.get().attributes
        sourceAttributes.keySet().forEach {
            attributeProvider(it as Attribute<Any>, provider { sourceAttributes.getAttribute(it) })
        }
    }
}
configurations.productionRuntimeClasspath { extendsFrom(productionOnly.get()) }

tasks.bootJar {
    classpath(productionOnlyPath)
}