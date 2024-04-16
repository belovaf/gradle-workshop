package workshop

import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import org.gradle.work.NormalizeLineEndings
import javax.inject.Inject

@CacheableTask
abstract class CompileTask : DefaultTask() {

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDir: DirectoryProperty

    @get:CompileClasspath
    abstract val classPath: ConfigurableFileCollection

    @get:Optional
    @get:Input
    abstract val release: Property<JavaVersion>

    @get:Optional
    @get:Input
    abstract val compilerArgs: ListProperty<String>

    @get:OutputDirectory
    abstract val classesDir: DirectoryProperty

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val fileOperations: FileOperations

    init {
        group = "build"
    }

    @TaskAction
    fun exec() {
        fileOperations.delete(classesDir)

        val result = execOperations.exec {
            executable("javac")

            args(compilerArgs.get())
            release.orNull?.let {
                args("--release", it.toString())
            }
            args("--source-path", sourceDir.get().asFile.path)
            args("-d", classesDir.get().asFile.path)
            if (!classPath.isEmpty) {
                args("--class-path", classPath.asPath)
            }
            args(sourceDir.asFileTree.map { it.path })
        }
        result.rethrowFailure()
    }
}