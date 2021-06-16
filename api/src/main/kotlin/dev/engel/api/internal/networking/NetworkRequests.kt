package dev.engel.api.internal.networking

import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.networking.request.MailchimpMemberRequest
import dev.engel.api.internal.networking.response.MailchimpError
import dev.engel.api.internal.networking.response.MailchimpMemberResponse
import dev.engel.api.internal.networking.response.YouTubeSearchResult
import dev.engel.api.internal.skribe.Skribe
import dev.engel.api.marketing.email.MailchimpApiKey
import dev.engel.api.youtube.YouTubeApiKey
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.opencensus.trace.Tracer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class NetworkRequests(
    private val httpClient: HttpClient = DependencyGraph.instance.httpClient,
    private val skribe: Skribe = DependencyGraph.instance.skribe,
    private val tracer: Tracer = DependencyGraph.instance.tracer,
) {
    suspend fun getRecentVideos(apiKey: YouTubeApiKey, limit: Int): YouTubeSearchResult {
        skribe.info("NetworkRequests#getRecentVideos")
        return try {
            tracer.spanBuilder("GetRecentVideos")
            val requestUrl = "https://www.googleapis.com/youtube/v3/search" +
                "?key=$apiKey" +
                "&channelId=UCXwjZTvpFiBl93ACCUh1NXQ" +
                "&type=video" +
                "&part=snippet" +
                "&order=date" +
                "&maxResults=$limit"

            httpClient.get<YouTubeSearchResult>(requestUrl) {
                accept(ContentType.Application.Json)
            }.also { skribe.info("recentVideos: $it") }
        } finally {
        	tracer.currentSpan.end()
        }
    }

    suspend fun postMailchimpMember(apiKey: MailchimpApiKey, mailchimpRequest: MailchimpMemberRequest): Pair<MailchimpMemberResponse?, MailchimpError?> {
        fun encodeAuthorizationHeader(apiKey: String): String {
            return Base64.getEncoder().encodeToString("user:$apiKey".toByteArray())
        }

        val requestUrl = "https://us1.api.mailchimp.com/3.0/lists/76fc4db729/members"

        return try {
            val response = httpClient.post<MailchimpMemberResponse>(requestUrl) {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                header("Authorization", "Basic ${encodeAuthorizationHeader(apiKey.key)}")
                body = mailchimpRequest
            }
            Pair(response, null)
        } catch (ex: ResponseException) {
            val text = ex.response.readText()
            skribe.error("ResponseError -- $text")
            val mailchimpError = Json { ignoreUnknownKeys = true }
                .decodeFromString<MailchimpError>(text)
            Pair(null, mailchimpError)
        }.also { skribe.info("postMailchimpMember: $it") }
    }
}
