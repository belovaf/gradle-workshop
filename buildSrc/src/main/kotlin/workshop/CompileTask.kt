package workshop

import org.gradle.api.JavaVersion
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class CompileTask : AbstractExecTask<CompileTask>(CompileTask::class.java) {

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourcePath: DirectoryProperty

    @get:Classpath
    abstract val classPath: ConfigurableFileCollection

    @get:Optional
    @get:Input
    abstract val release: Property<JavaVersion>

    @get:OutputDirectory
    abstract val classesDir: DirectoryProperty

    @get:Inject
    abstract val layout: ProjectLayout

    init {
        group = "build"
    }

    @TaskAction
    override fun exec() {
        executable = "javac"
        setArgs(buildArgs())
        super.exec()
    }

    private fun buildArgs(): List<String> = buildList {
        release.orNull?.let {
            add("--release")
            add(it.toString())
        }

        add("--source-path")
        add(sourcePath.get().asFile.toProjectRelativeString())

        add("-d")
        add(classesDir.get().asFile.toProjectRelativeString())

        if (!classPath.isEmpty) {
            add("--class-path")
            add(classPath.asPath)
        }

        sourcePath.asFileTree.mapTo(this) { it.toProjectRelativeString() }
    }

    private fun File.toProjectRelativeString(): String = toRelativeString(layout.projectDirectory.asFile)
}