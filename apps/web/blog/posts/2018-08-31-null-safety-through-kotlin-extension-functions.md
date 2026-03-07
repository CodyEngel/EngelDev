---
title: "Null Safety Through Kotlin Extension Functions"
date: 2018-08-31
description: How Kotlin extension functions can be defined on nullable types to handle null checks cleanly, with a real-world caching example.
tags:
  - kotlin
  - android
  - programming
---

One of the major selling points about Kotlin is the ability to avoid the dreaded `NullPointerException` (NPE), also known as [*The Billion Dollar Mistake*](https://en.wikipedia.org/wiki/Tony_Hoare#Apologies_and_retractions). Kotlin solves this problem by having a type system which can represent both nullable (indicated by a `?` at the end of the class name) and non-null types. However the language doesn't entirely eradicate NPEs, if you try hard enough (or call non-Kotlin source code) you can still make your application crash from accessing a `null` reference. Not only that, there are still plenty of valid reasons to still use `null` in our applications, so writing null safe code is *unfortunately* still part of our job descriptions.

![Photo by Amogh Manjunath on Unsplash](../../assets/images/source/2018-08-31-null-safety-through-kotlin-extension-functions-1.jpg "Photo by Amogh Manjunath on Unsplash")

Just the other day I was working on a caching system and found a great way to use (perhaps abuse) another feature of Kotlin known as [Extensions](https://kotlinlang.org/docs/reference/extensions.html) to call a function on a null reference without crashing. The typical way you might use an extension is when you want to add functionality to a class that you don't own, but they allow you call the function directly on that class. Behind the scenes an extension is resolved statically, if you have ever written a `Util` class then you are well aware of what this looks like. Since they are resolved statically though they give us another benefit, we can pass in a null reference and handle that without crashing; however at the callsite we are just calling the null-safe function on the object.

## To Load or Not to Load?

Let's say we want to make a network call to download an avatar which may change from time to time. We have a few options:

1. We can always request the image and waste bandwidth.
2. We can only request it once and risk showing stale data.
3. We can meet somewhere in the middle and download the image the first time and again only after some amount of time has passed.

In this case, let's go down the *meet in the middle* route. Let's consider a basic implementation of this:

```kotlin
data class ImageCache(
    val timeRetrieved: Long,
    val image: Image,
    val cacheExpiration: Long = 300000
)

object ImageLoader {

    val cacheMap = mutableMapOf<String, ImageCache>()

    fun load(resource: String): Image {
        val previousAttempt = cacheMap[resource]
        return previousAttempt?.run {
            if (timeRetrieved > (currentTime + cacheExpiration) {
                loadImage(resource)
            } else {
                image
            }
        } ?: loadImage(resource)
    }
}
```

This implementation should work, however it's not really the cleanest solution. First we have to handle the case where `previousAttempt` is null. Second, since we make a null safe call on `previousAttempt` we have to handle the situation when it is null which causes us to call `loadImage` a second time after the [elvis operator](https://en.wikipedia.org/wiki/Elvis_operator). Let's clean this up through the use of an extension function.

```kotlin
data class ImageCache(
    val timeRetrieved: Long,
    val image: Image,
    val cacheExpiration: Long = 300000
)

// Extension function added...
fun ImageCache?.isInvalidated(): Boolean {
    return this == null ||
        timeRetrieved > (currentTime + cacheExpiration)
}

object ImageLoader {

    val cacheMap = mutableMapOf<String, ImageCache>()

    fun load(resource: String): Image {
        val previousAttempt = cacheMap[resource]
        return if (previousAttempt.isInvalidated()) {
            loadImage(resource)
        } else {
            previousAttempt.image
        } // No need for Elvis now :D
    }
}
```

That's a much better solution. To start, we don't have to worry that `previousAttempt` is nullable, we can call the function `isInvalidated` in the same way we would call the non-null type for `ImageCache`. It also wraps the functionality to determine whether or not the cache is valid within the `ImageCache` itself, saving us from cluttering up code that just wants to know if the cache is still valid. Since this solution also removes the need to make a null safe call it saves us from the elvis operator that duplicated our `loadImage` call. Finally, we are calling the function as if it was part of `ImageCache` all along which helps with general readability.

## When Should We Use This?

At this point in time I'd rather err on the side of overuse than underuse when it comes to extensions that add null safe calls. If you find yourself writing code to check if an object is null before doing something else, then it might make sense to write an extension for it. The [extension functions for Strings](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) in Kotlin are a great place to look for additional inspiration (`isNullOrBlank()` isn't black magic, it's just an extension).

One last thing to call out, extensions are not just for functions, they work for properties too. Since no-op functions can almost always be rewritten as properties we could rewrite our function to be a property instead.

```kotlin
val ImageCache?.isInvalidated: Boolean
    get {
        return this == null ||
            timeRetrieved > (currentTime + cacheExpiration)
    }
```

If you found this technique useful in your own Kotlin code, feel free to share it with friends or colleagues.