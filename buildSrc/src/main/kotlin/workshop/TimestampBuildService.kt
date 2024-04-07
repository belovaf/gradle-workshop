package workshop

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

abstract class TimestampBuildService : BuildService<TimestampBuildService.Params> {
    val timestamp = Instant.now()
    val timestampString: String

    init {
        timestampString = parameters.format
            .map { DateTimeFormatter.ofPattern(it).withZone(ZoneId.systemDefault()) }
            .getOrElse(DateTimeFormatter.ISO_INSTANT)
            .format(timestamp)
    }

    interface Params : BuildServiceParameters {
        val format: Property<String>
    }
}