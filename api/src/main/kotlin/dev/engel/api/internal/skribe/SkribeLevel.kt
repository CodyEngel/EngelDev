package dev.engel.api.internal.skribe

@SuppressWarnings("MagicNumber")
sealed class SkribeLevel {
    val name: String = this::class.simpleName!!
    abstract val int: Int

    object Trace : SkribeLevel() {
        override val int: Int
            get() = 0
    }

    object Debug : SkribeLevel() {
        override val int: Int
            get() = 10
    }

    object Info : SkribeLevel() {
        override val int: Int
            get() = 20
    }

    object Warn : SkribeLevel() {
        override val int: Int
            get() = 30
    }

    object Error : SkribeLevel() {
        override val int: Int
            get() = 40
    }
}
