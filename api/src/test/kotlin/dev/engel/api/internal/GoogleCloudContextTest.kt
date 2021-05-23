package dev.engel.api.internal

import dev.engel.api.ApplicationEnvironment
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA

internal class GoogleCloudContextTest {

    @Test
    fun `given a local application environment, when GoogleCloudContext is created, then it should be a LocalGoogleCloudContext`() {
        expectThat(GoogleCloudContext(ApplicationEnvironment.LOCAL))
            .isA<LocalGoogleCloudContext>()
    }

    @Test
    fun `given a production application environment, when GoogleCloudContext is created, then it should be a ProductionGoogleCloudContext`() {
        expectThat(GoogleCloudContext(ApplicationEnvironment.PRODUCTION))
            .isA<ProductionGoogleCloudContext>()
    }
}
