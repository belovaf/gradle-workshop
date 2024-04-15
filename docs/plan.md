## Теория

- Declarative Gradle
  - [gradle roadmap](https://github.com/orgs/gradle/projects/31/views/1)
  - [restrictive dsl](https://blog.gradle.org/declarative-gradle)
  - avoid code in build.gradle.kts
    - bad examples:
      - https://github.com/spring-projects/spring-kafka/blob/main/build.gradle
      - https://github.com/spring-projects/spring-security/blob/main/build.gradle
  - use plugins, extensions

- Gradle build lifecycle
  - [diagram](images/build-lifecycle.png)
  - minimize logic executed during the initialization and configuration phase
  - defer logic to the execution phase

## Практика

### Java Plugin

#### Requirements
- lifecycle tasks (clean, check, assemble, build)
- compilation
- packaging to jar
- customizations:
  - java version
  - sources directory location
  - additional compiler arguments
- consume dependencies, implementation scope
- produce artifacts (other projects can consume our project as dependency)
- publish artifacts
- transitive compile dependencies, api scope
- produce sources jar
- optimization: do not build jar when not needed
- shared state: build timestamp

#### Steps
- Included builds, buildSrc

- Repositories
  - dependencyResolutionManagement
  - pluginManagement

- Base plugin
  - lifecycle tasks (clean, check, assemble, build)
  - explicit task dependencies

- Compilation without dependencies, build jar, run
  - [diagram](images/task-inputs-outputs.png)
  - configuration avoidance API (lazy configuration)
  - incremental builds
    - do not call clean task
  - caches:
    - task
    - build
    - configuration
  - task inputs / outputs
    - Property, Provider
    - input normalization (preview)
      - PathSensitive
  - task dependencies
    - implicit / explicit

- Java extension
  - extension DSL
  - convention
  - language version

- Configurations
  - (Configurable)FileCollection
  - (Configurable)FileTree
  - [file collection vs file tree diagram](images/file-collection-vs-file-tree.png)
  - Configuration: consumable, resolvable, dependencyScope
  - input normalization
    - PathSensitive
    - Classpath
    - CompileClasspath
    - content normalization, ABI, jar normalization
    - filtering, build-info.properties
  - [java plugin configurations](images/java-plugin-configurations.png)

- Consume artifacts
  - implementation
  - compileClasspath
  - runtimeClasspath
  - use apache commons text
  - runMain task

- Component model
  - [maven component model](images/component-model-maven.png)
  - [gradle component model](images/component-model-gradle.png)
  - components
  - outgoingVariants
  - attributes

- Produce artifacts
  - runtimeElements
  - use producer from consumer

- JavaLibraryPlugin:
- [java library plugin configurations](images/java-library-plugin-configurations.png)
  - api, compileClasspath, runtimeClasspath, apiElements
  - transitive dependencies: separate compile and runtime
  - compare with maven

- Advanced produce artifacts
  - sources jar variant
  - classes variant
  - AttributeCompatibilityRule for java version and library elements
  - AttributeDisambiguationRule for target jvm environment

- Shared build services
  - timestamp build service

### Java Convention Plugin

#### Requirements
- common compiler args
- common publication logic

#### Steps

- Convention plugins
- Precompiled script plugins
- Separate capabilities from conventions
  - JavaBasePlugin - capabilities
  - JavaPlugin - conventions

### Spring Boot productionOnly

#### Requirements

- productionOnly dependency scope

#### Steps

- build script dependency
- spring boot plugin productionRuntimeClasspath
- spring boot plugin bootJar
- reacting on other plugins
