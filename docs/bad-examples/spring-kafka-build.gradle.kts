buildscript {
    ext.kotlinVersion = '1.9.23'
    ext.isCI = System.getenv('GITHUB_ACTION')
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url 'https://repo.spring.io/plugins-release-local' }
        if (version.endsWith('SNAPSHOT')) {
            maven { url 'https://repo.spring.io/snapshot' }
        }
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        classpath "org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion"
    }
}

plugins {
    id 'base'
    id 'project-report'
    id 'idea'
    id 'org.ajoberstar.grgit' version '5.2.2'
    id 'io.spring.nohttp' version '0.0.11'
    id 'io.spring.dependency-management' version '1.1.4' apply false
    id 'com.github.spotbugs' version '6.0.12'
}

apply plugin: 'io.spring.nohttp'

description = 'Spring for Apache Kafka'

ext {
    linkHomepage = 'https://github.com/spring-projects/spring-kafka'
    linkCi = 'https://build.spring.io/browse/SK'
    linkIssue = 'https://github.com/spring-projects/spring-kafka/issues'
    linkScmUrl = 'https://github.com/spring-projects/spring-kafka'
    linkScmConnection = 'https://github.com/spring-projects/spring-kafka.git'
    linkScmDevConnection = 'git@github.com:spring-projects/spring-kafka.git'

    javadocLinks = [
        'https://docs.oracle.com/en/java/javase/17/docs/api/',
        'https://docs.spring.io/spring-framework/docs/current/javadoc-api/'
    ] as String[]


    modifiedFiles =
        files()
            .from {
                files(grgit.status().unstaged.modified)
                    .filter { f -> f.name.endsWith('.java') || f.name.endsWith('.kt') }
            }
    modifiedFiles.finalizeValueOnRead()

    assertjVersion = '3.25.3'
    awaitilityVersion = '4.2.1'
    hamcrestVersion = '2.2'
    hibernateValidationVersion = '8.0.1.Final'
    jacksonBomVersion = '2.17.0'
    jaywayJsonPathVersion = '2.9.0'
    junit4Version = '4.13.2'
    junitJupiterVersion = '5.10.2'
    kafkaVersion = '3.7.0'
    kotlinCoroutinesVersion = '1.8.0'
    log4jVersion = '2.23.1'
    micrometerDocsVersion = '1.0.2'
    micrometerVersion = '1.13.0-SNAPSHOT'
    micrometerTracingVersion = '1.3.0-SNAPSHOT'
    mockitoVersion = '5.10.0'
    reactorVersion = '2023.0.5'
    scalaVersion = '2.13'
    springBootVersion = '3.2.4' // docs module
    springDataVersion = '2024.0.0-SNAPSHOT'
    springRetryVersion = '2.0.5'
    springVersion = '6.1.6'
    zookeeperVersion = '3.8.4'

    idPrefix = 'kafka'

    javaProjects = subprojects - project(':spring-kafka-bom') - project(':spring-kafka-docs')

}

nohttp {
    source.include '**/src/**'
    source.exclude '**/*.gif', '**/*.ks', '**/.gradle/**'
}

allprojects {
    group = 'org.springframework.kafka'

    apply plugin: 'io.spring.dependency-management'

    repositories {
        mavenCentral()
        maven { url 'https://repo.spring.io/milestone' }
        if (version.endsWith('SNAPSHOT')) {
            maven { url 'https://repo.spring.io/snapshot' }
        }
//		maven { url 'https://repository.apache.org/content/groups/staging/' }
    }

    dependencyManagement {
        resolutionStrategy {
            cacheChangingModulesFor 0, 'seconds'
        }
        applyMavenExclusions = false
        generatedPomCustomization {
            enabled = false
        }

        imports {
            mavenBom "com.fasterxml.jackson:jackson-bom:$jacksonBomVersion"
            mavenBom "org.junit:junit-bom:$junitJupiterVersion"
            mavenBom "io.micrometer:micrometer-bom:$micrometerVersion"
            mavenBom "io.micrometer:micrometer-tracing-bom:$micrometerTracingVersion"
            mavenBom "io.projectreactor:reactor-bom:$reactorVersion"
            mavenBom "org.springframework.data:spring-data-bom:$springDataVersion"
            mavenBom "org.springframework:spring-framework-bom:$springVersion"
        }
    }
}

configure(javaProjects) { subproject ->
    apply plugin: 'java-library'
    apply plugin: 'java'
    apply from: "${rootProject.projectDir}/gradle/publish-maven.gradle"
    apply plugin: 'eclipse'
    apply plugin: 'idea'
    apply plugin: 'checkstyle'
    apply plugin: 'kotlin'
    apply plugin: 'kotlin-spring'

    java {
        withJavadocJar()
        withSourcesJar()
        registerFeature('optional') {
            usingSourceSet(sourceSets.main)
        }
    }

    compileJava {
        sourceCompatibility = 17
        targetCompatibility = 17
    }

    compileTestJava {
        sourceCompatibility = 17
        options.encoding = 'UTF-8'
    }

    eclipse.project.natures += 'org.springframework.ide.eclipse.core.springnature'

    // dependencies that are common across all java projects
    dependencies {
        def spotbugsAnnotations = "com.github.spotbugs:spotbugs-annotations:${spotbugs.toolVersion.get()}"
        compileOnly spotbugsAnnotations
                testCompileOnly spotbugsAnnotations

                testImplementation 'org.junit.jupiter:junit-jupiter-api'
        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

        // To avoid compiler warnings about @API annotations in JUnit code
        testCompileOnly 'org.apiguardian:apiguardian-api:1.0.0'

        testRuntimeOnly "org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion"

        testImplementation 'org.jetbrains.kotlin:kotlin-reflect'
        testImplementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
        testImplementation("org.awaitility:awaitility:$awaitilityVersion") {
            exclude group: 'org.hamcrest'
        }
        testImplementation "org.hamcrest:hamcrest-core:$hamcrestVersion"
        optionalApi "org.assertj:assertj-core:$assertjVersion"
    }

    // enable all compiler warnings; individual projects may customize further
    [compileJava, compileTestJava]*.options*.compilerArgs = ['-Xlint:all,-options,-processing', '-parameters']

    test {
        testLogging {
            events "skipped", "failed"
            showStandardStreams = project.hasProperty("showStandardStreams") ?: false
            showExceptions = true
            showStackTraces = true
            exceptionFormat = 'full'
        }

        maxHeapSize = '1536m'
        useJUnitPlatform()

    }

    checkstyle {
        configDirectory.set(rootProject.file("src/checkstyle"))
        toolVersion = '10.12.5'
    }

    publishing {
        publications {
            mavenJava(MavenPublication) {
                suppressAllPomMetadataWarnings()
                from components.java
            }
        }
    }

    task updateCopyrights {
        onlyIf { !isCI }
        inputs.files(modifiedFiles.filter { f -> f.path.contains(subproject.name) })
        outputs.dir('build/classes')

        doLast {
            def now = Calendar.instance.get(Calendar.YEAR) as String
            inputs.files.each { file ->
                def line
                        file.withReader { reader ->
                            while (line = reader.readLine()) {
                                def matcher = line =~ /Copyright (20\d\d)-?(20\d\d)?/
                                if (matcher.count) {
                                    def beginningYear = matcher[0][1]
                                    if (now != beginningYear && now != matcher[0][2]) {
                                        def years = "$beginningYear-$now"
                                        def sourceCode = file.getText('UTF-8')
                                        sourceCode = sourceCode.replaceFirst(/20\d\d(-20\d\d)?/, years)
                                        file.write(sourceCode)
                                        println "Copyright updated for file: $file"
                                    }
                                    break
                                }
                            }
                        }
            }
        }
    }

    compileKotlin.dependsOn updateCopyrights

            jar {
                manifest {
                    attributes(
                        'Implementation-Version': archiveVersion,
                        'Created-By': "JDK ${System.properties['java.version']} (${System.properties['java.specification.vendor']})",
                    'Implementation-Title': subproject.name,
                    'Implementation-Vendor-Id': subproject.group,
                    'Implementation-Vendor': 'VMware Inc.',
                    'Implementation-URL': linkHomepage,
                    'Automatic-Module-Name': subproject.name.replace('-', '.')  // for Jigsaw
                    )
                }

                from("${rootProject.projectDir}/src/dist") {
                    include 'notice.txt'
                    into 'META-INF'
                    expand(copyright: new Date().format('yyyy'), version: project.version)
                }
                from("${rootProject.projectDir}") {
                    include 'LICENSE.txt'
                    into 'META-INF'
                }
            }

    tasks.withType(Javadoc) {
        options.addBooleanOption('Xdoclint:syntax', true) // only check syntax with doclint
        options.addBooleanOption('Werror', true) // fail build on Javadoc warnings
    }

}

project ('spring-kafka') {
    description = 'Spring Kafka Support'

    dependencies {
        api 'org.springframework:spring-context'
        api 'org.springframework:spring-messaging'
        api 'org.springframework:spring-tx'
        api ("org.springframework.retry:spring-retry:$springRetryVersion") {
            exclude group: 'org.springframework'
        }
        api "org.apache.kafka:kafka-clients:$kafkaVersion"
        optionalApi "org.apache.kafka:kafka-streams:$kafkaVersion"
        optionalApi "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion"
        optionalApi 'com.fasterxml.jackson.core:jackson-core'
        optionalApi 'com.fasterxml.jackson.core:jackson-databind'
        optionalApi 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8'
        optionalApi 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'
        optionalApi 'com.fasterxml.jackson.datatype:jackson-datatype-joda'
        optionalApi ('com.fasterxml.jackson.module:jackson-module-kotlin') {
            exclude group: 'org.jetbrains.kotlin'
        }

        // Spring Data projection message binding support
        optionalApi ("org.springframework.data:spring-data-commons") {
            exclude group: 'org.springframework'
            exclude group: 'io.micrometer'
        }
        optionalApi "com.jayway.jsonpath:json-path:$jaywayJsonPathVersion"

        optionalApi 'io.projectreactor:reactor-core'
        optionalApi 'io.projectreactor.kafka:reactor-kafka'
        optionalApi 'io.micrometer:micrometer-core'
        api 'io.micrometer:micrometer-observation'
        optionalApi 'io.micrometer:micrometer-tracing'

        testImplementation project (':spring-kafka-test')
        testImplementation 'io.projectreactor:reactor-test'
        testImplementation "org.mockito:mockito-junit-jupiter:$mockitoVersion"
        testImplementation "org.hibernate.validator:hibernate-validator:$hibernateValidationVersion"
        testImplementation ('io.micrometer:micrometer-observation-test') {
            exclude group: "org.mockito"
        }
        testImplementation 'io.micrometer:micrometer-tracing-bridge-brave'
        testImplementation 'io.micrometer:micrometer-tracing-test'
        testImplementation ('io.micrometer:micrometer-tracing-integration-test') {
            exclude group: "org.mockito"
        }
    }
}

project('spring-kafka-bom') {
    description = 'Spring for Apache Kafka (Bill of Materials)'

    apply plugin: 'java-platform'
    apply from: "${rootDir}/gradle/publish-maven.gradle"

    dependencies {
        constraints {
            javaProjects.sort { "$it.name" }.each {
                if (it.name != 'spring-kafka-docs') {
                    api it
                }
            }
        }
    }

    publishing {
        publications {
            mavenJava(MavenPublication) {
                from components.javaPlatform
            }
        }
    }
}

project ('spring-kafka-test') {
    description = 'Spring Kafka Test Support'

    dependencies {
        api 'org.springframework:spring-context'
        api 'org.springframework:spring-test'
        api ("org.springframework.retry:spring-retry:$springRetryVersion") {
            exclude group: 'org.springframework'
        }

        api ("org.apache.zookeeper:zookeeper:$zookeeperVersion") {
            exclude group: 'org.slf4j', module: 'slf4j-log4j12'
            exclude group: 'log4j'
        }
        api "org.apache.kafka:kafka-clients:$kafkaVersion:test"
        api "org.apache.kafka:kafka-metadata:$kafkaVersion"
        api "org.apache.kafka:kafka-server-common:$kafkaVersion"
        api "org.apache.kafka:kafka-server-common:$kafkaVersion:test"
        api "org.apache.kafka:kafka-streams-test-utils:$kafkaVersion"
        api ("org.apache.kafka:kafka_$scalaVersion:$kafkaVersion") {
            exclude group: 'commons-logging'
        }
        api ("org.apache.kafka:kafka_$scalaVersion:$kafkaVersion:test") {
            exclude group: 'commons-logging'
        }
        api 'org.junit.jupiter:junit-jupiter-api'
        api 'org.junit.platform:junit-platform-launcher'
        optionalApi "org.hamcrest:hamcrest-core:$hamcrestVersion"
        optionalApi "org.mockito:mockito-core:$mockitoVersion"
        optionalApi ("junit:junit:$junit4Version") {
            exclude group: 'org.hamcrest', module: 'hamcrest-core'
        }
        optionalApi "org.apache.logging.log4j:log4j-core:$log4jVersion"
    }
}

configurations {
    micrometerDocs
}

dependencies {
    micrometerDocs "io.micrometer:micrometer-docs-generator:$micrometerDocsVersion"
}

def observationInputDir = file('spring-kafka/src/main/java/org/springframework/kafka/support/micrometer').absolutePath
def generatedDocsDir = file('build/docs/generated').absolutePath

tasks.register('generateObservabilityDocs', JavaExec) {
    mainClass = 'io.micrometer.docs.DocsGeneratorCommand'
    inputs.dir(observationInputDir)
    outputs.dir(generatedDocsDir)
    classpath configurations.micrometerDocs
            args observationInputDir, /.+/, generatedDocsDir
}

tasks.register('filterMetricsDocsContent', Copy) {
    dependsOn generateObservabilityDocs
            from generatedDocsDir
            include '_*.adoc'
    into generatedDocsDir
            rename { filename -> filename.replace '_', '' }
    filter { line -> line.replaceAll('org.springframework.kafka.support.micrometer.', '').replaceAll('^Fully qualified n', 'N') }
}

tasks.register('api', Javadoc) {
    group = 'Documentation'
    description = 'Generates aggregated Javadoc API documentation.'
    title = "${rootProject.description} ${version} API"
    options {
        encoding = 'UTF-8'
        memberLevel = org.gradle.external.javadoc.JavadocMemberLevel.PROTECTED
        author = true
        header = rootProject.description
        use = true
        overview = 'src/api/overview.html'
        splitIndex = true
        links(project.ext.javadocLinks)
        addBooleanOption('Xdoclint:syntax', true) // only check syntax with doclint
    }

    source javaProjects.collect { project ->
        project.sourceSets.main.allJava
    }

    classpath = files(javaProjects.collect { project ->
        project.sourceSets.main.compileClasspath
    })
    destinationDir = file('build/api')
}

tasks.register('docsZip', Zip) {
    group = 'Distribution'
    archiveClassifier = 'docs'
    description = "Builds -${archiveClassifier} archive containing api and reference " +
            "for deployment at static.spring.io/spring-kafka/docs."

    from('src/dist') {
        include 'changelog.txt'
    }

    from(api) {
        into 'api'
    }
}

tasks.register('distZip', Zip) {
    dependsOn 'docsZip'
    group = 'Distribution'
    archiveClassifier = 'dist'
    description = "Builds -${archiveClassifier} archive, containing all jars and docs, " +
            "suitable for community download page."

    ext.baseDir = "${project.name}-${project.version}"

    from('src/dist') {
        include 'readme.txt'
        include 'notice.txt'
        into "${baseDir}"
    }

    from("$project.rootDir") {
        include 'LICENSE.txt'
        into "${baseDir}"
    }

    from(zipTree(docsZip.archiveFile)) {
        into "${baseDir}/docs"
    }

    javaProjects.each { subproject ->
        into ("${baseDir}/libs") {
            from subproject.jar
                    from subproject.sourcesJar
                    from subproject.javadocJar
        }
    }

    from(project(':spring-kafka-bom').generatePomFileForMavenJavaPublication) {
        into "${baseDir}/libs"
        rename 'pom-default.xml', "spring-kafka-bom-${project.version}.xml"
    }

}

tasks.register('dist') {
    dependsOn assemble
            group = 'Distribution'
    description = 'Builds -dist, -docs distribution archives.'
}

apply from: "${rootProject.projectDir}/gradle/publish-maven.gradle"

publishing {
    publications {
        mavenJava(MavenPublication) {
            artifact docsZip
                    artifact distZip
        }
    }
}