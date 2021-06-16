package dev.engel.api.youtube

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import io.opencensus.trace.Tracer
import kotlinx.serialization.Serializable
import java.util.*

inline class YouTubeApiKey(val key: String) {
    override fun toString(): String = key
}

class YouTubeVideoRepository(
    private val datastore: Datastore = DependencyGraph.instance.datastore,
    private val retrieveRecentYouTubeVideos: RetrieveRecentYouTubeVideos = RetrieveRecentYouTubeVideos(),
    private val writeRecentYouTubeVideos: WriteRecentYouTubeVideos = WriteRecentYouTubeVideos(),
    private val skribe: Skribe = DependencyGraph.instance.skribe,
    private val tracer: Tracer = DependencyGraph.instance.tracer
) {

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
        return try {
            tracer.spanBuilder("RetrieveYouTubeVideos").startScopedSpan()
            fetchedAt?.let { date ->
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
                    retrieveVideosFromCache(limit)
                }
            } ?: retrieveVideosFromNetwork(limit)
        } finally {
            tracer.currentSpan.end()
        }
    }

    private suspend fun retrieveVideosFromNetwork(limit: Int): List<YouTubeVideo> {
        return try {
            tracer.spanBuilder("RetrieveVideosFromNetwork")
            retrieveRecentYouTubeVideos.fromNetwork(limit)
                .also { writeRecentYouTubeVideos.write(it) }
                .also { updateFetchedAt() }
        } finally {
        	tracer.currentSpan.end()
        }
    }

    private suspend fun retrieveVideosFromCache(limit: Int): List<YouTubeVideo> {
        return try {
            tracer.spanBuilder("RetrieveVideosFromCache")
            retrieveRecentYouTubeVideos.fromCache(limit)
        } finally {
        	tracer.currentSpan.end()
        }
    }

    private fun updateFetchedAt() {
        skribe.info("YouTubeVideoRepository#updateFetchedAt")
        try {
            tracer.spanBuilder("UpdateFetchedAt")
            val key = datastore.newKeyFactory()
                .setKind("LastFetchedYouTubeVideo")
                .newKey()

            val entity = Entity.newBuilder(key)
                .set("fetchedAt", Timestamp.now())
                .build()

            datastore.put(entity)
        } finally {
            tracer.currentSpan.end()
        }
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
