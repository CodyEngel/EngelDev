package dev.engel.api.internal.di

import com.google.cloud.datastore.Datastore
import dev.engel.api.internal.GoogleCloudContext
import dev.engel.api.internal.skribe.Skribe
import dev.engel.api.marketing.email.MailchimpApiKey
import dev.engel.api.youtube.YouTubeApiKey
import io.ktor.client.*
import io.opencensus.trace.Tracer
import io.opencensus.trace.Tracing

class DependencyGraph(
    val youTubeApiKey: YouTubeApiKey,
    val mailchimpApiKey: MailchimpApiKey,
    val googleCloudContext: GoogleCloudContext,
    val httpClient: HttpClient,
    val skribe: Skribe,
    val tracer: Tracer = Tracing.getTracer(),
) {
    val datastore: Datastore = googleCloudContext.datastore

    companion object {
        @Suppress("ObjectPropertyName")
        private var _instance: DependencyGraph? = null
        val instance: DependencyGraph
            get() = _instance!!

        fun initialize(
            youTubeApiKey: YouTubeApiKey,
            mailchimpApiKey: MailchimpApiKey,
            googleCloudContext: GoogleCloudContext,
            httpClient: HttpClient,
            skribe: Skribe,
        ) {
            _instance = DependencyGraph(
                youTubeApiKey,
                mailchimpApiKey,
                googleCloudContext,
                httpClient,
                skribe
            )
        }
    }
}
