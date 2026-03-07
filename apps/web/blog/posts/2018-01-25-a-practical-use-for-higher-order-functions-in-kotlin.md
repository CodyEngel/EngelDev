---
title: "A practical use for higher order functions in Kotlin."
date: 2018-01-25
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# A practical use for higher order functions in Kotlin.

I’ve been working with Kotlin for a little over a year. From
<a href="/@CodyEngel/lets-learn-how-to-code-part-000-396aec959f5b"
class="z ph" rel="noopener" data-discover="true">writing tutorials about
Kotlin</a> to <a
href="https://www.slideshare.net/CodyEngel/privet-kotlin-windy-city-devfest"
class="z ph" rel="noopener ugc nofollow" target="_blank">giving talks at
local meet-ups</a> and conferences, it’s safe to say I at least feel
comfortable with the language. One thing I never fully grasped with
Kotlin were higher order functions. It’s not that I didn’t understand
how to use them, however I didn’t understand why I’d inject
functionality into another function, it just seemed strange.

I recently transitioned away from Android at work and started working in
Ruby. I think Kotlin gave me a solid foundation for understanding some
of the concepts behind Ruby. One of those concepts I learned about are
called blocks which essentially work like higher order functions, Ruby
Koans gives it the friendly name of <a
href="https://github.com/javierjulio/ruby-koans-completed/blob/master/about_sandwich_code.rb"
class="z ph" rel="noopener ugc nofollow" target="_blank">sandwich
code</a>. That’s when I finally connected the dots and understood the
pratical usage of higher order functions. Aside from doubling numbers
(ask 

<a
href="/u/5179950c9486?source=post_page---user_mention--7ff89cebf6fd---------------------------------------"
class="pi pj fi" rel="noopener" target="_blank"
data-discover="true">Nick Cruz</a>

about that), you can also wrap a function around boring code such as
database transactions, or ensuring when you open a file you also close
it.

<figure class="pl pm pn po pp pk bd paragraph-image">
<img src="_media/492528_image1.jpg" loading="eager"
role="presentation" />
<figcaption><a href="https://unsplash.com/@eaterscollective"
class="z ph" rel="noopener ugc nofollow" target="_blank">Eaters
Collective via Unsplash</a>.</figcaption>
</figure>

## You had me at practical use…

I’m currently working on benchmarking programming languages to determine
which one would perform the best for API related activities (interacting
with a database, marshaling objects, handling network requests, etc),
part of that means I have to record how long a given function runs.
Kotlin makes that code incredibly efficient through higher order
functions. Let’s say I want to determine how long it takes to create and
print 100,000 `Person` instances. I could simply write code that looks
like this:

```bash
val startTime = System.currentTimeMillis()
for (i in 0..100000) {
    println("Person: ${Person()}")
}
val endTime = System.currentTimeMillis()
println("Executed in ${endTime - startTime}ms")
```

That works great except the code that deals with the calculating runtime
has to be copied and pasted everytime I want to benchmark something
else. However since we can pass blocks of code into functions with
Kotlin we can simply create a `Benchmark` class that will handle the
beginning and end, all you have to do is supply what goes in the middle.

```bash
class Benchmark {
    fun benchmark(block: () -> Unit): Long {
        val startTime = System.currentTimeMillis()
        block.invoke()
        return System.currentTimeMillis() - startTime
    }
}
```

The foreign things about the above code is mostly with how we pass in
the function and then invoke the function. Whenever you want to take a
function as an argument you will give it the type of `() -> Unit` which
says that this function doesn’t take any parameters and returns the type
of `Unit` (the default return type in Kotlin). Certainly you could
change the type but for this article let’s keep it simple.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

So how do you invoke the function? In this case I’ve assigned it to a
variable named `block` as it’s representing a block of code. So you’ll
notice that I then call `block.invoke()` which is the way you tell
Kotlin to run the function.

Then the code we want to benchmark can be updated to look like this:

```bash
val runtime = benchmark.benchmark {
    for (i in 0..100000) {
        println("Person: ${Person()}")
    }
}

println("Executed in ${runtime}ms")
```

This of course isn’t the DRYest code, we could also include the
`println` as part of our `benchmark` function, however I feel like this
example should get main point across. When you find yourself copying
code to encapsulate other code (acting like the bread of a sandwich) it
might be a good idea to move that code into it’s own class that just
takes a function.

This isn’t the only use for higher order functions, and if you have
other examples I’d be happy to read about them in the responses. However
in my case this is something that helped me understand **why** I would
want to use a higher order function.

