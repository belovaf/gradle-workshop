package workshop

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.time.Instant

abstract class TimestampBuildService : BuildService<BuildServiceParameters.None> {
    val timestamp = Instant.now()
}