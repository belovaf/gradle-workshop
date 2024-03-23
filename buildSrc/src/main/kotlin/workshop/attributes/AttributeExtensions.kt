package workshop.attributes

import org.gradle.api.Named
import org.gradle.api.Project

inline fun <reified T : Named> Project.attr(value: String) = objects.named(T::class.java, value)