package muontrap

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import kotlin.system.exitProcess

class App {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        @ObsoleteCoroutinesApi
        @InternalCoroutinesApi
        @JvmStatic
        fun main(args: Array<String>) {
            val cancel = CliHelper.trapSignal("INT")
            val conf = ConfigFactory.load()
            val config = AppConfiguration(conf)
            val nop = NopTicker(conf)
            logger.debug("config:$config")
            runBlocking {
                logger.info("CROSSING.THE.STREAMS")
                val jobs = mutableListOf<Job>(launch(Dispatchers.IO) { nop.run() })

                // Wait for cancel signal
                select<Unit> {
                    cancel.onReceive {
                        logger.info("STREAMS.CROSSED")
                        jobs.forEach { it.cancelAndJoin() }
                        logger.info("PROTON.PACK.OVERLOAD")
                    }
                }
            } //-runBlocking
            logger.info("TOTAL.PROTONIC.REVERSAL")

            exitProcess(0)
        } //-main
    } //-companion

} //-App
