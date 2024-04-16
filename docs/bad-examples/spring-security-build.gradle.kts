import io.spring.gradle.IncludeRepoTask
import trang.RncToXsd

buildscript {
    dependencies {
        classpath libs.io.spring.javaformat.spring.javaformat.gradle.plugin
                classpath libs.io.spring.nohttp.nohttp.gradle
                classpath libs.io.freefair.gradle.aspectj.plugin
                classpath libs.org.jetbrains.kotlin.kotlin.gradle.plugin
                classpath libs.com.netflix.nebula.nebula.project.plugin
    }
    repositories {
        maven { url 'https://plugins.gradle.org/m2/' }
    }
}

plugins {
    alias(libs.plugins.org.gradle.wrapper.upgrade)
}

apply plugin: 'io.spring.nohttp'
apply plugin: 'locks'
apply plugin: 's101'
apply plugin: 'io.spring.convention.root'
apply plugin: 'org.jetbrains.kotlin.jvm'
apply plugin: 'org.springframework.security.versions.verify-dependencies-versions'
apply plugin: 'io.spring.security.release'

group = 'org.springframework.security'
description = 'Spring Security'

ext.snapshotBuild = version.contains("SNAPSHOT")
ext.releaseBuild = version.contains("SNAPSHOT")
ext.milestoneBuild = !(snapshotBuild || releaseBuild)

repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/milestone" }
}

springRelease {
    weekOfMonth = 3
    dayOfWeek = 1
    referenceDocUrl = "https://docs.spring.io/spring-security/reference/{version}/index.html"
    apiDocUrl = "https://docs.spring.io/spring-security/docs/{version}/api/"
    replaceSnapshotVersionInReferenceDocUrl = true
}

def toolchainVersion() {
    if (project.hasProperty('testToolchain')) {
        return project.property('testToolchain').toString().toInteger()
    }
    return 17
}

subprojects {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(toolchainVersion())
        }
    }
    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    tasks.withType(JavaCompile).configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.release.set(17)
    }
}

allprojects {
    if (!['spring-security-bom', 'spring-security-docs'].contains(project.name)) {
        apply plugin: 'io.spring.javaformat'
        apply plugin: 'checkstyle'

        pluginManager.withPlugin("io.spring.convention.checkstyle", { plugin ->
            configure(plugin) {
                dependencies {
                    checkstyle libs.io.spring.javaformat.spring.javaformat.checkstyle
                }
                checkstyle {
                    toolVersion = '8.34'
                }
            }
        })

        if (project.name.contains('sample')) {
            tasks.whenTaskAdded { task ->
                if (task.name.contains('format') || task.name.contains('checkFormat') || task.name.contains("checkstyle")) {
                    task.enabled = false
                }
            }
        }
    }
}

if (hasProperty('buildScan')) {
    buildScan {
        termsOfServiceUrl = 'https://gradle.com/terms-of-service'
        termsOfServiceAgree = 'yes'
    }
}

nohttp {
    source.exclude "buildSrc/build/**"
    source.builtBy(project(':spring-security-config').tasks.withType(RncToXsd))
}

tasks.register('cloneRepository', IncludeRepoTask) {
    repository = project.getProperties().get("repositoryName")
    ref = project.getProperties().get("ref")
    var defaultDirectory = project.file("build/tmp/clone")
    outputDirectory = project.hasProperty("cloneOutputDirectory") ? project.file("$cloneOutputDirectory") : defaultDirectory
}

s101 {
    repository = 'https://structure101.com/binaries/latest'
    configurationDirectory = project.file("etc/s101")
}

wrapperUpgrade {
    gradle {
        'spring-security' {
            repo = 'spring-projects/spring-security'
            baseBranch = '6.1.x' // runs only on 6.1.x and the update is merged forward to main
        }
    }
}