package dev.engel.api.youtube

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.skribe.Skribe
import io.opencensus.trace.Tracer

class WriteRecentYouTubeVideos(
	private val datastore: Datastore = DependencyGraph.instance.datastore,
	private val skribe: Skribe = DependencyGraph.instance.skribe,
	private val tracer: Tracer = DependencyGraph.instance.tracer,
) {
	fun write(youTubeVideos: List<YouTubeVideo>) {
		skribe.info("WriteRecentYouTubeVideos#write")
		tracer.spanBuilder("WriteRecentYouTubeVideos").startScopedSpan().use {
			datastore.newBatch().apply {
				youTubeVideos.forEach { youTubeVideo ->
					val key = datastore.newKeyFactory()
						.setKind("YouTubeVideo")
						.newKey(youTubeVideo.id)

					val entity = Entity.newBuilder(key)
						.set("id", youTubeVideo.id)
						.set("title", youTubeVideo.title)
						.set("description", youTubeVideo.description)
						.set("publishedAt", youTubeVideo.publishedAt)
						.set("thumbnail", youTubeVideo.thumbnail)
						.build()
					put(entity)
				}
			}.submit()
		}
	}
}
