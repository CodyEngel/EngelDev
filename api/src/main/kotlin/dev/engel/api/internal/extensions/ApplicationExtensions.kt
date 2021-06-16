package dev.engel.api.internal.extensions

import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import io.ktor.application.*
import io.opencensus.trace.Tracer

val Application.skribe: Skribe
    get() = DependencyGraph.instance.skribe

val Application.tracer: Tracer
    get() = DependencyGraph.instance.tracer
