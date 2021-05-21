package dev.engel.api.youtube

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe

class WriteRecentYouTubeVideos(
    private val datastore: Datastore = DependencyGraph.instance.datastore,
    private val skribe: Skribe = DependencyGraph.instance.skribe
) {
    fun write(youTubeVideos: List<YouTubeVideo>) {
        skribe.info("WriteRecentYouTubeVideos#write")
        youTubeVideos.map { youTubeVideo ->
            val key = datastore.newKeyFactory()
                .setKind("YouTubeVideo")
                .newKey(youTubeVideo.id)

            // TODO: not testable, needs a facade
            Entity.newBuilder(key)
                .set("id", youTubeVideo.id)
                .set("title", youTubeVideo.title)
                .set("description", youTubeVideo.description)
                .set("publishedAt", youTubeVideo.publishedAt)
                .set("thumbnail", youTubeVideo.thumbnail)
                .build()
        }.forEach { datastore.put(it) }
    }
}