package dev.engel.api.youtube

import dev.engel.api.internal.extensions.skribe
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

private lateinit var youTubeVideoRepository: YouTubeVideoRepository

fun Application.registerYouTubeRoutes() {
    youTubeVideoRepository = YouTubeVideoRepository()
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

@SuppressWarnings("MagicNumber")
private suspend fun retrieveRecentVideos(): YouTubeVideos {
    val videos = youTubeVideoRepository.retrieveVideos(21)
    return YouTubeVideos(videos)
}

@Serializable
data class YouTubeVideos(
    val videos: List<YouTubeVideo>
)
