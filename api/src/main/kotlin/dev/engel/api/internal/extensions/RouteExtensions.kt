package dev.engel.api.internal.extensions

import dev.engel.api.internal.skribe.Skribe
import io.ktor.routing.*

val Route.skribe: Skribe
    get() = application.skribe
