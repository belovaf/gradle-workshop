package workshop

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.Serializable
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.function.Supplier

abstract class BuildTimestampService : BuildService<BuildTimestampService.Params> {
    val timestamp = Instant.now()
    val timestampString: String

    init {
        timestampString = parameters.format
            .getOrElse { DateTimeFormatter.ISO_INSTANT }
            .get()
            .format(timestamp)
    }

    interface Params : BuildServiceParameters {
        val format: Property<DateTimeFormatterSupplier>
    }
}

fun interface DateTimeFormatterSupplier : Supplier<DateTimeFormatter>, Serializable