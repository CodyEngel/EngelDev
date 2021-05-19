package dev.engel.api.internal.extensions

import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import io.ktor.application.*

val Application.skribe: Skribe
    get() = DependencyGraph.instance.skribe