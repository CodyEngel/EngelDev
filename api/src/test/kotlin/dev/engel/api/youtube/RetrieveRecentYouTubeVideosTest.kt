package dev.engel.api.youtube

import com.google.cloud.datastore.Datastore
import dev.engel.api.internal.networking.*
import dev.engel.api.internal.skribe.Skribe
import dev.engel.api.testcommons.random.generateRandomInt
import dev.engel.api.testcommons.random.generateRandomList
import dev.engel.api.testcommons.random.generateRandomString
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo


@Suppress("EXPERIMENTAL_API_USAGE")
internal class RetrieveRecentYouTubeVideosTest {

    private val apiKey = YouTubeApiKey(generateRandomString())
    private val datastore: Datastore = mockk(relaxed = true)
    private val networkRequests: NetworkRequests = mockk(relaxed = true)
    private val skribe: Skribe = mockk(relaxed = true)

    private val subject = RetrieveRecentYouTubeVideos(
        apiKey = apiKey,
        datastore = datastore,
        networkRequests = networkRequests,
        skribe = skribe
    )

    @Test
    fun `when a network request is successful, it should return the correct video`() {
        val expectedId = generateRandomString()
        val expectedTitle = generateRandomString()
        val expectedDescription = generateRandomString()
        val expectedPublishedAt = generateRandomString()
        val expectedThumbnailUrl = generateRandomString()

        val searchResultItems = listOf(
            createYouTubeSearchResultItem(
                expectedId = expectedId,
                expectedTitle = expectedTitle,
                expectedDescription = expectedDescription,
                expectedPublishedAt = expectedPublishedAt,
                expectedThumbnailUrl = expectedThumbnailUrl
            )
        )
        coEvery {
            networkRequests.getRecentVideos(apiKey, any())
        } returns createYouTubeSearchResult(searchResultItems)

        runBlockingTest {
            expectThat(subject.fromNetwork(1).first()) {
                get("has correct id") { id } isEqualTo expectedId
                get("has correct title") { title } isEqualTo expectedTitle
                get("has correct description") { description } isEqualTo expectedDescription
                get("has correct published at") { publishedAt } isEqualTo expectedPublishedAt
                get("has correct thumbnail") { thumbnail } isEqualTo expectedThumbnailUrl
            }
        }
    }

    @Test
    fun `when a network request is successful, it should return the correct number of videos`() {
        val size = 21
        val searchResultItems = generateRandomList(size) { createYouTubeSearchResultItem() }
        coEvery {
            networkRequests.getRecentVideos(apiKey, any())
        } returns createYouTubeSearchResult(searchResultItems)

        runBlockingTest {
            expectThat(subject.fromNetwork(size)).hasSize(size)
        }
    }

    private fun createYouTubeSearchResult(items: List<YouTubeSearchResultItem> = emptyList()) : YouTubeSearchResult {
        return YouTubeSearchResult(
            etag = generateRandomString(),
            nextPageToken = generateRandomString(),
            pageInfo = PageInfo(
                totalResults = generateRandomInt(),
                resultsPerPage = generateRandomInt()
            ),
            items = items
        )
    }

    private fun createYouTubeSearchResultItem(
        expectedId: String = generateRandomString(),
        expectedTitle: String = generateRandomString(),
        expectedDescription: String = generateRandomString(),
        expectedPublishedAt: String = generateRandomString(),
        expectedThumbnailUrl: String = generateRandomString(),
    ): YouTubeSearchResultItem {
        return YouTubeSearchResultItem(
            etag = generateRandomString(),
            id = YouTubeId(
                kind = generateRandomString(),
                videoId = expectedId
            ),
            snippet = YouTubeSearchSnippet(
                title = expectedTitle,
                description = expectedDescription,
                publishedAt = expectedPublishedAt,
                thumbnails = mapOf(
                    "medium" to YouTubeThumbnail(
                        url = expectedThumbnailUrl,
                        width = 270,
                        height = 270
                    )
                )
            )
        )
    }
}
