---
title: "The Beauty Of The KotlinNullPointerException"
date: 2018-10-02
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# The Beauty Of The KotlinNullPointerException

It seems like ever day I’m finding more reasons to enjoy Kotlin
development. It first started when I heard Kotlin eradicates the
possibility of the dreaded NullPointerException (false information, but
hey it was something to get excited about). Then I saw how elegant it
was to model information using the `data` class and as an RxJava user I
found `sealed` classes an easy way to model streams of data. As of late
though I’ve found myself diving deeper into the intricacies of the
language and stumbling upon ways of writing concise code that is safer
than the verbose Java code I was writing just a year ago.

Today I think I finally found a legitimate reason to use the `!!`
operator. For those unfamiliar, this is the operator to use when you
want to ask for a `KotlinNullPointerException`, it is the only way
(aside from calling platform code) that you can get such an exception.
In the time I’ve been writing Kotlin it is also frowned upon to use, but
I hope this small example will change a couple minds.

<figure class="pt pu pv pw px pk py pz paragraph-image">

Press enter or
click to view image in full size

<img src="_media/318705_image1.jpg" loading="eager"
role="presentation" />

<figcaption>Photo by <a
href="https://unsplash.com/photos/QkSN_8XcXwQ?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z qj" rel="noopener ugc nofollow" target="_blank">Niketh
Vellanki</a> on <a
href="https://unsplash.com/search/photos/space?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z qj" rel="noopener ugc nofollow"
target="_blank">Unsplash</a></figcaption>
</figure>

First though, let’s look at what I was going to write today while
running on auto-pilot trying to avoid any trace of an `NPE`:

```bash
val classWithNullableProperties = ClassWithNullableProperties()
return if (classWithNullableProperties.propertyOne != null &&
    classWithNullableProperties.propertyTwo != null &&
    classWithNullableProperties.propertyThree != null &&
    classWithNullableProperties.propertyFour != null &&
    classWithNullableProperties.propertyFive != null &&
    classWithNullableProperties.propertySix != null) {
    RepositoryResponse.Success(
        Record(
            classWithNullableProperties.propertyOne,
            classWithNullableProperties.propertyTwo,
            classWithNullableProperties.propertyThree,
            classWithNullableProperties.propertyFour,
            classWithNullableProperties.propertyFive,
            classWithNullableProperties.propertySix
        )
    )
} else {
    RepositoryResponse.Error("Uh Oh, Something Was Null")
}
```

The code above is a whole lot of ugly, couldn’t Kotlin do any better?
I’d like to think it could, thought it might be interesting to catch the
`KotlinNullPointerException` and just return an error if that happens.
Since conditions are evaluated as expressions this is possible and seems
to be supported by the language creators. By doing just that we are able
to clean things up substantially:

```bash
val classWithNullableProperties = ClassWithNullableProperties()
return try {
    RepositoryResponse.Success(
        Record(
            classWithNullableProperties.propertyOne!!,
            classWithNullableProperties.propertyTwo!!,
            classWithNullableProperties.propertyThree!!,
            classWithNullableProperties.propertyFour!!,
            classWithNullableProperties.propertyFive!!,
            classWithNullableProperties.propertySix!!
        )
    )
} catch (ex: KotlinNullPointerException) {
    RepositoryResponse.Error("Uh Oh, Something Was Null")
}
```

This solution seems to go against all of my old beliefs on dealing with
null pointer exceptions as I have actively tried avoiding them by
checking if something is null before using them. This of course is
nothing new if you are coming from Java, you can always just catch
a`NullPointerException` and have the same outcome. I think the
difference between Kotlin and Java comes down to explicitly asking for a
`NullPointerException` on each property that you *know* can be null.
This difference is really only possible because nullability is built
into the type system of Kotlin.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

That’s it. This was a fairly short example that I wanted to share
because I think it was a bit out there and the conventional null safety
mechanisms wouldn’t have been sufficient, or at least they would have
lead to some very verbose Java-ese looking code.

Having the freedom to work with Kotlin and avoid Java almost entirely
has been a breath of fresh air for me. I feel very lucky to be working
at a company like ActiveCampaign that is so forward thinking. We’re also
growing like crazy, if you’re as excited about Kotlin as I am then
<a href="https://www.activecampaign.com/about/careers" class="z qj"
rel="noopener ugc nofollow" target="_blank">check out our current job
postings</a>.

*Originally published at* <a
href="https://www.activecampaign.com/blog/inside-activecampaign/the-beauty-of-the-kotlinnullpointerexception/"
class="z qj" rel="noopener ugc nofollow"
target="_blank"><em>www.activecampaign.com</em></a>*.*

