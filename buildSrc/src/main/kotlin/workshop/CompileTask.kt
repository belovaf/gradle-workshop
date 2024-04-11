package workshop

import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class CompileTask : DefaultTask() {

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourcePath: DirectoryProperty

    @get:Classpath
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
    protected abstract val exec: ExecOperations

    init {
        group = "build"
    }

    @TaskAction
    fun exec() {
        val result = exec.exec {
            executable("javac")

            args(compilerArgs.get())
            release.orNull?.let {
                args("--release", it.toString())
            }
            args("--source-path", sourcePath.get().asFile.toProjectRelativeString())
            args("-d", classesDir.get().asFile.toProjectRelativeString())
            if (!classPath.isEmpty) {
                args("--class-path", classPath.asPath)
            }
            args(sourcePath.asFileTree.map { it.toProjectRelativeString() })
        }
        result.rethrowFailure()
    }

    private fun File.toProjectRelativeString(): String = toRelativeString(layout.projectDirectory.asFile)
}