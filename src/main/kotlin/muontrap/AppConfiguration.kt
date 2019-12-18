package muontrap

import com.typesafe.config.Config

class AppConfiguration(val config: Config) {
    val throughput = config.getDouble("application.throughput")

    override fun toString(): String {
        return """
throughput=$throughput,
        """.trimIndent()
    }
}