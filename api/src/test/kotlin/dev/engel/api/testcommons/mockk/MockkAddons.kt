package dev.engel.api.testcommons.mockk

import io.mockk.MockKVerificationScope
import io.mockk.verify

fun verifyOnce(verifyBlock: MockKVerificationScope.() -> Unit) {
    verify(exactly = 1, verifyBlock = verifyBlock)
}