---
title: "The Curious Case Of The Channel That Wouldn’t Quit"
date: 2018-12-04
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# The Curious Case Of The Channel That Wouldn’t Quit

I have been working on a new top secret Android app at
<a href="https://www.activecampaign.com/" class="z pg"
rel="noopener ugc nofollow" target="_blank">ActiveCampaign</a> which
recently started to crash randomly. Thankfully the application is still
completely internal so it wasn’t a top priority to resolve the problem.
Today however I found myself with most things already complete on the
release checklist so `Prevent App From Constantly ANRing` was at the top
of the list. The following is a dramatized story about my past failures
when I was diving into the world of Kotlin coroutines.

<figure class="pm pn po pp pq pl bd paragraph-image">
<img src="_media/46560_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a href="https://unsplash.com/photos/0Q33pyk-AXI"
class="z pg" rel="noopener ugc nofollow" target="_blank">Tina
Rataj-Berard</a></figcaption>
</figure>

## Use Coroutines They Said…

I was implementing an authentication flow which involved a number of
callbacks which needed to be handled. If I was using `RxJava` it would
have been easy enough to wrap them into an `Observable` and call it a
day, unfortunately I decided to try out coroutines and avoid `Rx`
completely. Callbacks are asynchronous in nature, you define what you
want to run when a function is called but you have no idea when it will
be called. With `Rx` you model this through an `ObservableSource`, in
Java you can use a `CountDownLatch` to wait for the callback to execute,
in Kotlin you can use a `Channel`.

The default behavior of both an `ObservableSource` and `CountDownLatch`
is to define the asynchronous behavior first and then define the code
that executes afterwards after that. Let’s say we want to wait for an
`Int` to be returned from our asynchronous source, using `Rx` we might
have something like this:

```bash
val observable = Observable.fromCallable { it.onNext(1) }
observable.subscribe { println("Number: $it") }
```

If we wanted to use Coroutines instead we could rewrite the same code to
instead be this:

```bash
val channel = Channel<Int>()
channel.sendBlocking(1) // we don't want to suspend when sending
GlobalScope.launch { println("Number: ${channel.receive()}") }
```

The code looks innocent and yet it will cause your application to lock
up and cease to function. The error might be easy to spot when it’s all
in one place, however when the `Channel` being used is distributed
between classes it becomes much harder to spot. Before talking about a
solution let me point out two other implementations of this same code
which will execute perfectly fine.

```bash
val channel = Channel<Int>()
GlobalScope.launch { println("Number: ${channel.receive()}") }
channel.sendBlocking(1) // we don't want to suspend when sending// This will also work perfectly fineval channel = Channel<Int>()
GlobalScope.launch { channel.send(1) }
GlobalScope.launch { println("Number: ${channel.receive()}")
```

## Alright So What Was The Problem?

There are multiples problems with my original implementation and it
could be resolved a number of ways. The root cause though was me not
knowing enough about channels, so before I talk about the problem let’s
learn about the different channels available to us. I’ve summarized the
different channels below and want to thank

<a
href="/u/a5e37380c850?source=post_page---user_mention--15fea9a29145---------------------------------------"
class="rj rk fi" rel="noopener" target="_blank"
data-discover="true">Marek Langiewicz</a>

for doing an <a
href="https://blog.elpassion.com/create-a-clean-code-app-with-kotlin-coroutines-and-android-architecture-components-part-2-4f585050d7d7"
class="z pg" rel="noopener ugc nofollow" target="_blank">awesome
write-up involving coroutines</a>.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

**RendezvousChannel**, it does not contain any internal buffer for
messages. Every `send` invocation is suspended until a `receive`
function is invoked (or if a `receive` operation has been suspended).
Every `receive` invocation is suspended until a `send` function is
invoked (or if a `send` operation has been suspended).

**ArrayChannel** — it contains a fixed buffer size. Suspension only
happens on `send` if the buffer is full. Suspension only happens on
`receive` if the buffer is empty.

**LinkedListChannel** — it has unlimited capacity. The `send`
invocations will never be suspended. The `receive` invocations will only
be suspended when the buffer is empty.

**ConflatedChannel** — it buffers at most one element and combines all
subsequent `send` invocations. Invoking `send` never suspends, but new
elements will override old elements waiting to be received. Invoking
`receive` will suspend when the buffer is empty.

Those are the different types of channels available to us today. The
coroutines library contains a helper function `Channel` which works like
a factory to give you the correct type of channel depending on the
capacity you specify. If you pass in no value it will default to a
`RendezvousChannel`. This means `send` will suspend until the value is
received; this means if you invoke `sendBlocking` it will block its
current thread until `receive` is invoked. That is why the original code
snippet fails, it blocks the current thread waiting for `receive` to be
invoked however that function is never invoked since the thread blocks
before it can be invoked. Using any other Channel the problem would not
happen (or would be less likely to occur), or if we only invoked the
`send` call from a suspending function.

## So What Was My Fix?

I ended up resolving this problem by changing two things about the
previous implementation. The first thing I did was remove the
`sendBlocking` call, it has no place in the `apk` you deploy to devices.
I replaced that by invoking the `send` function from a `CoroutineScope`
which prevents our code from blocking the main thread and thus causing
the app to crash. I then updated the `Channel` being used to a
`ConflatedChannel` which provides the behavior I want (when I invoke
`receive` just give me the most recent thing `sent` or wait until
something arrives).

To make this solution easily scannable here is the easy breakdown:

1.  Don’t use `sendBlocking` outside of unit tests, and
    even then think twice.
2.  Use the correct type of `Channel`, there are four
    which is more than the one I thought existed.

Coroutines are awesome, and so are channels. However like any tool you
choose to use it’s important to understand how they work. The problem I
ran into was easy to stumble into, however it would have been easily
avoided had I done a bit more research before implementing them. If you
want to try out the different channels I created a <a
href="https://gist.github.com/CodyEngel/29c52c19f5c3b3265953f517fb6c0dc3"
class="z pg" rel="noopener ugc nofollow" target="_blank">gist</a> that
you can drop into IntelliJ or Android Studio and play around with.

*Thanks for taking the time to read through my article. If you found
something to be not quite right or have other information to add please
reach out in the comments section below. If you enjoyed this article,
please click on the clap icon a few times or share it on social media
(or both). Lastly, I’m starting up a mailing list that is powered by*
<a href="https://www.activecampaign.com/" class="z pg"
rel="noopener ugc nofollow" target="_blank"><em>ActiveCampaign</em></a>*,
if you want to get weekly newsletters then*
<a href="https://upscri.be/fac601/" class="z pg"
rel="noopener ugc nofollow" target="_blank"><em>please use the sign-up
form located here</em></a>*.*

