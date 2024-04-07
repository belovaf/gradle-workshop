### Теория

Gradle roadmap
- restrictive dsl (https://blog.gradle.org/declarative-gradle)
- avoid code in build.gradle.kts
  - bad example: https://github.com/spring-projects/spring-security/blob/main/build.gradle
- use plugins, extensions

Gradle build lifecycle
- ! minimize logic executed during the initialization and configuration phase
- defer logic to the execution phase

### Практика
- Included builds, buildSrc
- Base plugin
  - lifecycle tasks
  - explicit task dependencies
- Compilation without dependencies, build jar, run
  - configuration avoidance API (lazy configuration)
  - task inputs / outputs
  - property, provider
  - implicit / explicit task dependencies
  - caches
  - incremental builds, do not call clean

- Java extension, sourceCompatibility, targetCompatibility
  - extension DSL

- Consume artifacts, implementation, classpath
  - (Configurable)FileCollection
  - (Configurable)FileTree
  - Configuration: consumable, resolvable, dependencyScope
  - PathSensitive

- Produce artifacts, runtimeElements
  - components
  - outgoingVariants
  - attributes

- JavaLibraryPlugin: api, compileClasspath, runtimeClasspath, apiElements
  - transitive dependencies
  - compare with maven

- Advanced produce artifacts
  - java-doc variant
  - AttributeCompatibilityRule for version and library elements
  - ?secondary variants

- Shared build services
  - timestamp build service

- Convention plugins, reuse build configuration
  - capabilities vs conventions
  - opinionated java library plugin

- SpringBootPlugin productionOnly

- Разбор community plugins

- 
### Advanced
Incremental task inputs
Parallel tasks with Worker API


### ???
Platforms, version catalogs
Advanced testing capabilities
Build services (version timestamp)


### Spring boot thin plugin
Module - thin
 ov -> thin jar
 ov -> dependencies

distribution
  module1
  module2