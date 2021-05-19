package dev.engel.api.youtube

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import java.util.*

inline class YouTubeApiKey(val key: String) {
    override fun toString(): String = key
}

class YouTubeVideoRepository(
    apiKey: YouTubeApiKey = DependencyGraph.instance.youTubeApiKey,
    private val datastore: Datastore = DependencyGraph.instance.datastore,
    private val httpClient: HttpClient = DependencyGraph.instance.httpClient,
    private val skribe: Skribe = DependencyGraph.instance.skribe,
) {
    private val requestUrl = "https://www.googleapis.com/youtube/v3/search?key=$apiKey&channelId=UCXwjZTvpFiBl93ACCUh1NXQ&type=video&part=snippet&order=date"

    private val fetchedAt: Date?
        get() {
            skribe.info("YouTubeVideoRepository#fetchedAt")
            val query = Query.newEntityQueryBuilder().setKind("LastFetchedYouTubeVideo")
                .setOrderBy(StructuredQuery.OrderBy.desc("fetchedAt"))
                .setLimit(1)
                .build()
            val queryResult = datastore.run(query)
            return if (queryResult.hasNext()) {
                queryResult.next()
                    .also { skribe.info("ENTITY -- $it") }
                    .getTimestamp("fetchedAt")
                    .toDate()
            } else {
                skribe.info("ENTITY -- null")
                null
            }
        }

    suspend fun retrieveVideos(limit: Int = 21): List<YouTubeVideo> {
        skribe.info("YouTubeVideoRepository#retrieveVideos")
        return fetchedAt?.let { date ->
            val now = Date()
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.HOUR, 1)
            val cacheExpires = calendar.time

            skribe.info("TIME (now) -- ${now.time}")
            skribe.info("TIME (fetchedAt) -- ${date.time}")
            skribe.info("TIME (cacheExpires) -- ${cacheExpires.time}")

            if (now.after(cacheExpires)) {
                retrieveVideosFromNetwork(limit)
            } else {
                retrieveVideosFromDataStore(limit)
            }
        } ?: retrieveVideosFromNetwork(limit)
    }

    private suspend fun retrieveVideosFromNetwork(limit: Int): List<YouTubeVideo> {
        skribe.info("YouTubeVideoRepository#retrieveVideosFromNetwork")
        val youTubeSearchResponse = httpClient.get<YouTubeSearchResult>("$requestUrl&maxResults=$limit") {
            accept(ContentType.Application.Json)
        }

        val videos = youTubeSearchResponse.items.map { youTubeSearchResultItem ->
            val snippet = youTubeSearchResultItem.snippet
            YouTubeVideo(
                id = youTubeSearchResultItem.id.videoId,
                title = snippet.title,
                description = snippet.description,
                publishedAt = snippet.publishedAt,
                thumbnail = snippet.thumbnails.getValue("medium").url
            )
        }
        writeVideosToDataStore(videos)

        return videos
    }

    private fun retrieveVideosFromDataStore(limit: Int): List<YouTubeVideo> {
        skribe.info("YouTubeVideoRepository#retrieveVideosFromDataStore")
        val youtubeVideoQuery = Query.newEntityQueryBuilder().setKind("YouTubeVideo")
            .setOrderBy(StructuredQuery.OrderBy.desc("publishedAt"))
            .setLimit(limit)
            .build()

        val result = datastore.run(youtubeVideoQuery)
        val videos = mutableListOf<YouTubeVideo>()
        result.forEach { entity ->
            val video = YouTubeVideo(
                id = entity.getString("id"),
                title = entity.getString("title"),
                description = entity.getString("description"),
                publishedAt = entity.getString("publishedAt"),
                thumbnail = entity.getString("thumbnail")
            )
            videos.add(video)
        }

        return videos
    }

    private fun writeVideosToDataStore(youTubeVideos: List<YouTubeVideo>) {
        skribe.info("YouTubeVideoRepository#writeVideosToDataStore")
        youTubeVideos.map { youTubeVideo ->
            val key = datastore.newKeyFactory()
                .setKind("YouTubeVideo")
                .newKey(youTubeVideo.id)

            Entity.newBuilder(key)
                .set("id", youTubeVideo.id)
                .set("title", youTubeVideo.title)
                .set("description", youTubeVideo.description)
                .set("publishedAt", youTubeVideo.publishedAt)
                .set("thumbnail", youTubeVideo.thumbnail)
                .build()
        }.forEach { datastore.put(it) }
            .also { updateFetchedAt() }
    }

    private fun updateFetchedAt() {
        skribe.info("YouTubeVideoRepository#updateFetchedAt")
        val key = datastore.newKeyFactory()
            .setKind("LastFetchedYouTubeVideo")
            .newKey()

        val entity = Entity.newBuilder(key)
            .set("fetchedAt", Timestamp.now())
            .build()

        datastore.put(entity)
    }

}

@Serializable
data class YouTubeVideo(
    val id: String,
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnail: String,
)

@Serializable
internal data class YouTubeSearchResult(
    val etag: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val items: List<YouTubeSearchResultItem>
)

@Serializable
internal data class YouTubeSearchResultItem(
    val etag: String,
    val id: YouTubeId,
    val snippet: YouTubeSearchSnippet
)

@Serializable
internal data class YouTubeSearchSnippet(
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnails: Map<String, YouTubeThumbnail>
)

@Serializable
internal data class YouTubeThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
internal data class YouTubeId(
    val kind: String,
    val videoId: String
)

@Serializable
internal data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)