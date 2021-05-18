package dev.engel.api.internal.skribe

import dev.engel.api.testcommons.mockk.verifyOnce
import dev.engel.api.testcommons.random.randomString
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.slf4j.Logger

internal class SLF4JSkribeTest {
    private val logger = mockk<Logger>(relaxed = true)

    private val subject = SLF4JSkribe(logger)

    @Test
    fun `when trace is invoked, then the logger's trace should be invoked`() = randomString { randomString ->
        subject.trace(randomString)

        verifyOnce { logger.trace(randomString) }
    }

    @Test
    fun `when debug is invoked, then the logger's debug should be invoked`() = randomString { randomString ->
        subject.debug(randomString)

        verifyOnce { logger.debug(randomString) }
    }

    @Test
    fun `when info is invoked, then the logger's info should be invoked`() = randomString { randomString ->
        subject.info(randomString)

        verifyOnce { logger.info(randomString) }
    }

    @Test
    fun `when warn is invoked, then the logger's warn should be invoked`() = randomString { randomString ->
        subject.warn(randomString)

        verifyOnce { logger.warn(randomString) }
    }

    @Test
    fun `when error is invoked, then the logger's error should be invoked`() = randomString { randomString ->
        subject.error(randomString)

        verifyOnce { logger.error(randomString) }
    }
}