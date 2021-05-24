package dev.engel.api.internal.networking.response

import kotlinx.serialization.Serializable

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
