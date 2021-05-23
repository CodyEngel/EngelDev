package dev.engel.api.internal.skribe

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class SkribeLevelTest {
    private val trace = SkribeLevel.Trace
    private val debug = SkribeLevel.Debug
    private val info = SkribeLevel.Info
    private val warn = SkribeLevel.Warn
    private val error = SkribeLevel.Error

    @Test
    fun `given a Trace level, when name is invoked, then it should be equal to Trace`() {
        expectThat(trace.name)
            .isEqualTo("Trace")
    }

    @Test
    fun `given a Debug level, when name is invoked, then it should be equal to Debug`() {
        expectThat(debug.name)
            .isEqualTo("Debug")
    }

    @Test
    fun `given a Info level, when name is invoked, then it should be equal to Info`() {
        expectThat(info.name)
            .isEqualTo("Info")
    }

    @Test
    fun `given a Warn level, when name is invoked, then it should be equal to Warn`() {
        expectThat(warn.name)
            .isEqualTo("Warn")
    }

    @Test
    fun `given a Error level, when name is invoked, then it should be equal to Error`() {
        expectThat(error.name)
            .isEqualTo("Error")
    }
}
