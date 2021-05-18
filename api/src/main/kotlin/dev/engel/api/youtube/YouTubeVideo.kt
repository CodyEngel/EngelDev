package dev.engel.api.youtube

import dev.engel.api.internal.GoogleCloudContext
import dev.engel.api.internal.extensions.skribe
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class YouTubeVideos(
    val videos: List<YouTubeVideo>
)

private lateinit var youTubeVideoRepository: YouTubeVideoRepository

fun Application.registerYouTubeRoutes(apiKey: String, googleCloudContext: GoogleCloudContext) {
    youTubeVideoRepository = YouTubeVideoRepository(apiKey, googleCloudContext, skribe)
    routing {
        youtubeRecentVideoRouting()
    }
}

fun Route.youtubeRecentVideoRouting() {
    route("/youtube/recentVideos") {
        get {
            application.skribe.info("GET -- YouTube Recent Videos")
            call.respond(retrieveRecentVideos())
        }
    }
}

private suspend fun retrieveRecentVideos(): YouTubeVideos {
    val videos = youTubeVideoRepository.retrieveVideos(21)
    return YouTubeVideos(videos)
}
