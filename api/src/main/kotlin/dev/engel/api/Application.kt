package dev.engel.api

import dev.engel.api.internal.GoogleCloudContext
import dev.engel.api.marketing.email.registerEmailRoutes
import dev.engel.api.youtube.registerYouTubeRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.main() {
    install(CallLogging) {
        level = environment.callLoggingLevel
    }
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
        allowNonSimpleContentTypes = true // needed for post request: application/json;charset=UTF-8
    }

    val mailchimpKey = environment.config.property("ktor.mailchimpApiKey").getString()
    val youtubeKey = environment.config.property("ktor.youtubeApiKey").getString()
    val applicationEnvironment = environment.applicationEnvironment
    val googleCloudContext = GoogleCloudContext(applicationEnvironment)

    registerEmailRoutes(mailchimpKey)
    registerYouTubeRoutes(youtubeKey, googleCloudContext)
}

enum class ApplicationEnvironment {
    PRODUCTION,
    LOCAL
}

private val KtorApplicationEnvironment.applicationEnvironment: ApplicationEnvironment
    get() = when (config.propertyOrNull("ktor.environment")?.getString()?.toLowerCase()) {
        "local" -> ApplicationEnvironment.LOCAL
        else -> ApplicationEnvironment.PRODUCTION
    }

private val KtorApplicationEnvironment.callLoggingLevel: Level
    get() {
        return when (applicationEnvironment) {
            ApplicationEnvironment.PRODUCTION -> Level.INFO
            ApplicationEnvironment.LOCAL -> Level.TRACE
        }
    }

typealias KtorApplicationEnvironment = io.ktor.application.ApplicationEnvironment
