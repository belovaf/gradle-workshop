package workshop.attributes

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.LibraryElements

internal class LibraryElementsCompatibilityRules : AttributeCompatibilityRule<LibraryElements> {
    override fun execute(details: CompatibilityCheckDetails<LibraryElements>) {
        val consumerValue = details.consumerValue ?: return
        val producerValue = details.producerValue ?: return
        if (consumerValue.name == LibraryElements.CLASSES && producerValue.name == LibraryElements.JAR) {
            details.compatible()
        }
    }
}