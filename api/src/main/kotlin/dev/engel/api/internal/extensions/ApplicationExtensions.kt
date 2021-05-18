package dev.engel.api.internal.extensions

import dev.engel.api.internal.skribe.SLF4JSkribe
import dev.engel.api.internal.skribe.Skribe
import io.ktor.application.*

val Application.skribe: Skribe
    get() = SLF4JSkribe(environment.log)