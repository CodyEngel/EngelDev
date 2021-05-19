package dev.engel.api

import dev.engel.api.internal.GoogleCloudContext
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.SLF4JSkribe
import dev.engel.api.marketing.email.MailchimpApiKey
import dev.engel.api.marketing.email.registerEmailRoutes
import dev.engel.api.youtube.YouTubeApiKey
import dev.engel.api.youtube.registerYouTubeRoutes
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.slf4j.Logger
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

    val mailchimpApiKey = environment.config.property("ktor.mailchimpApiKey").getString()
    val youTubeApiKey = environment.config.property("ktor.youtubeApiKey").getString()
    initializeDependencyGraph(
        youTubeApiKey = youTubeApiKey,
        mailchimpApiKey = mailchimpApiKey,
        logger = environment.log,
        applicationEnvironment = environment.applicationEnvironment
    )

    registerEmailRoutes()
    registerYouTubeRoutes()
}

private fun initializeDependencyGraph(
    youTubeApiKey: String,
    mailchimpApiKey: String,
    logger: Logger,
    applicationEnvironment: ApplicationEnvironment
) {
    DependencyGraph.initialize(
        youTubeApiKey = YouTubeApiKey(youTubeApiKey),
        mailchimpApiKey = MailchimpApiKey(mailchimpApiKey),
        googleCloudContext = GoogleCloudContext(applicationEnvironment),
        httpClient = createHttpClient(),
        skribe = SLF4JSkribe(logger)
    )
}

private fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
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
