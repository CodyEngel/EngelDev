package dev.engel.api.internal.skribe

interface Skribe {
    fun trace(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun warn(message: String)

    fun error(message: String)
}