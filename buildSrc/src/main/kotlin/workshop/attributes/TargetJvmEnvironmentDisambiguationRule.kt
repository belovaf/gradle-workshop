package workshop.attributes

import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.MultipleCandidatesDetails
import org.gradle.api.attributes.java.TargetJvmEnvironment

class TargetJvmEnvironmentDisambiguationRule : AttributeDisambiguationRule<TargetJvmEnvironment> {
    override fun execute(details: MultipleCandidatesDetails<TargetJvmEnvironment>) {
        val consumerValue = details.consumerValue
        if (consumerValue != null && details.candidateValues.contains(consumerValue)) {
            details.closestMatch(consumerValue)
        } else {
            val standardJvm = details.candidateValues.firstOrNull {
                it.name == TargetJvmEnvironment.STANDARD_JVM
            }
            if (standardJvm != null) {
                details.closestMatch(standardJvm)
            }
        }
    }
}