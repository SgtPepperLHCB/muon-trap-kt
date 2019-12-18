package muontrap

import com.typesafe.config.Config
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.selects.select
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

class NopTicker(conf: Config) {
    val config = AppConfiguration(conf)
    val telemetry = PkeMeter(config, "nop")

    @ObsoleteCoroutinesApi
    @InternalCoroutinesApi
    suspend fun run() {
        logger.info("run")

        val ticktock = ticker(delayMillis = (1000.0 / config.throughput).toLong(), initialDelayMillis = 0)
        var tick = false
        while (isActive) {
            select<Unit> {
                ticktock.onReceive {
                    try {
                        telemetry.attempts()

                        logger.info(if (tick) "tick" else "tock")
                        tick = !tick

                        telemetry.successes()
                    } catch (e: Exception) {
                        telemetry.errors(e)
                    }
                } //-ticktock
            } //-select
        } //-while

        logger.info("EXIT")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}