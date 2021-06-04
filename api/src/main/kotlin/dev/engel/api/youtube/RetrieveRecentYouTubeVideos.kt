package dev.engel.api.youtube

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.networking.NetworkRequests
import dev.engel.api.internal.skribe.Skribe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class RetrieveRecentYouTubeVideos(
    private val apiKey: YouTubeApiKey = DependencyGraph.instance.youTubeApiKey,
    private val datastore: Datastore = DependencyGraph.instance.datastore,
    private val networkRequests: NetworkRequests = NetworkRequests(),
    private val skribe: Skribe = DependencyGraph.instance.skribe,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun fromNetwork(limit: Int): List<YouTubeVideo> {
        skribe.info("RetrieveRecentYouTubeVideos#retrieveVideosFromNetwork")
        val youTubeSearchResponse = networkRequests.getRecentVideos(apiKey, limit)

        return youTubeSearchResponse.items.map { youTubeSearchResultItem ->
            val snippet = youTubeSearchResultItem.snippet
            YouTubeVideo(
                id = youTubeSearchResultItem.id.videoId,
                title = snippet.title,
                description = snippet.description,
                publishedAt = snippet.publishedAt,
                thumbnail = "https://img.youtube.com/vi/${youTubeSearchResultItem.id.videoId}/maxresdefault.jpg"
            )
        }.also { skribe.info("youTubeVideos: $it") }
    }

    suspend fun fromCache(limit: Int): List<YouTubeVideo> = coroutineScope {
        skribe.info("RetrieveRecentYouTubeVideos#retrieveVideosFromDataStore")
        withContext(ioDispatcher) {
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

            videos
        }
    }
}
