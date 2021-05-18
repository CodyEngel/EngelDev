package dev.engel.api.testcommons.random

import java.util.*

fun generateRandomString(): String = UUID.randomUUID().toString()

fun randomString(block: (randomString: String) -> Unit) {
    block(generateRandomString())
}