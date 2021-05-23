package dev.engel.api.internal.skribe

import org.slf4j.Logger

class SLF4JSkribe(private val logger: Logger) : Skribe {
    override fun trace(message: String) {
        logger.trace(message)
    }

    override fun debug(message: String) {
        logger.debug(message)
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}
