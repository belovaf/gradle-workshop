package workshop.attributes

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails

class JavaVersionCompatibilityRule : AttributeCompatibilityRule<Int> {
    override fun execute(details: CompatibilityCheckDetails<Int>) {
        val consumerValue = details.consumerValue ?: return
        val producerValue = details.producerValue ?: return
        if (consumerValue > producerValue) {
            details.compatible()
        }
    }
}