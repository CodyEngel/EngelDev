package dev.engel.api.internal.networking

import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import dev.engel.api.youtube.YouTubeApiKey
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class NetworkRequests(
    private val httpClient: HttpClient = DependencyGraph.instance.httpClient,
    private val skribe: Skribe = DependencyGraph.instance.skribe
) {
    suspend fun getRecentVideos(apiKey: YouTubeApiKey, limit: Int): YouTubeSearchResult {
        skribe.info("NetworkRequests#getRecentVideos")

        val requestUrl = "https://www.googleapis.com/youtube/v3/search" +
                "?key=$apiKey" +
                "&channelId=UCXwjZTvpFiBl93ACCUh1NXQ" +
                "&type=video" +
                "&part=snippet" +
                "&order=date" +
                "&maxResults=$limit"

        return httpClient.get<YouTubeSearchResult>(requestUrl) {
            accept(ContentType.Application.Json)
        }.also { skribe.info("recentVideos: $it") }
    }
}

@Serializable
data class YouTubeSearchResult(
    val etag: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val items: List<YouTubeSearchResultItem>
)

@Serializable
data class YouTubeSearchResultItem(
    val etag: String,
    val id: YouTubeId,
    val snippet: YouTubeSearchSnippet
)

@Serializable
data class YouTubeSearchSnippet(
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnails: Map<String, YouTubeThumbnail>
)

@Serializable
data class YouTubeThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
data class YouTubeId(
    val kind: String,
    val videoId: String
)

@Serializable
data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)