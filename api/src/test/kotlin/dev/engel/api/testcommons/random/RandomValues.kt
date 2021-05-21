package dev.engel.api.testcommons.random

import java.util.*

fun generateRandomString(): String = UUID.randomUUID().toString()

fun randomString(block: (randomString: String) -> Unit) {
    block(generateRandomString())
}

fun generateRandomInt(min: Int = 0, max: Int = 50): Int = (min..max).random()

fun randomInt(min: Int = 0, max: Int = 50, block: (randomInt: Int) -> Unit) {
    block(generateRandomInt(min, max))
}

fun <T> generateRandomList(size: Int = generateRandomInt(), creator: (index: Int) -> T): List<T> {
    return (0 until size).map(creator)
}