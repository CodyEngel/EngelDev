---
title: "Managing Disposables in RxJava 2 — The Less Bad Version"
date: 2018-12-06
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


![](data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgZmlsbD0ibm9uZSIgdmlld2JveD0iMCAwIDE2IDE2Ij48cGF0aCBzdHJva2U9ImN1cnJlbnRDb2xvciIgZD0ibTEyLjAwMSAxMS4wOTQtLjE5OC4xODcuMDUuMjY4YTUuMSA1LjEgMCAwIDAgLjgwOCAxLjk0NCA0LjA3IDQuMDcgMCAwIDEtMi4wMTctLjY4MWwtLjAwNi0uMDA0YTQgNCAwIDAgMS0uNDkzLS4zNjhsLS4yMDItLjE3Ni0uMjU5LjA3MWMtLjUyOS4xNDYtMS4wNzUuMjItMS42MjQuMjE4aC0uMDAxYy0zLjExNSAwLTUuNTU5LTIuMjkzLTUuNTU5LTUuMDI2QzIuNSA0LjggNC45NDUgMi41IDguMDUxIDIuNWMzLjA5NSAwIDUuNDQ4IDIuMjkgNS40NDggNS4wMjd2LjAwOWE0Ljc4IDQuNzggMCAwIDEtMS40OTggMy41NThaIiAvPjwvc3ZnPg==)

1

# Managing Disposables in RxJava 2 — The Less Bad Version

I wrote an article about managing disposables back in 2017. This was my
first stab at trying to work with the new callbacks and wanting to
create a boiler-plate free way to work with them. I made a rather
glaring mistake though, the entire fictitious application has a single
`CompositeDisposable` which means when one controller is destroyed it
would try `dispose` which will prematurely dispose all of your streams.
Yikes, that’s definitely not a good idea. <a
href="/@CodyEngel/managing-disposables-in-rxjava-2-for-android-388722ae1e8a"
class="z pl" rel="noopener" data-discover="true">The article spawned a
lot of great discussion</a> and I didn’t see much reason to provide an
update, if you check the comments section then you’ll quickly know it’s
a bad idea after-all. Unfortunately that article started ranking really
well on Google (it’s now number one for the query *managing disposables*
or *composite disposable* and is one of my most viewed articles on
Medium); it’s time to provide alternative solutions.

<figure class="po pp pq pr ps pn bd paragraph-image">
<img src="_media/602573_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a href="https://unsplash.com/photos/EXpa6pyXkHA"
class="z pl" rel="noopener ugc nofollow" target="_blank">Bas
Emmen</a></figcaption>
</figure>

## Scoping Disposables Is Important

Unless you want bad things to happen it’s important that your
disposables aren’t globally scoped and you can dispose of some without
disposing of everything. In the Android world I’ve found that having one
`CompositeDisposable` per view controller (`Activity`/`Fragment`) as
well as one per `ViewModel` seems to work out fairly well. This means we
want something that looks something like this…

```bash
class ExampleViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()    fun doSomething() {
        val disposable = Single.just(1)
            .subscribe { 
                //update something onNext
            }
        compositeDisposable.add(disposable)
    }    override fun onCleared() {
        compositeDisposable.clear()
    }
}
```

This will work out fine however in every ViewModel we are forced to
create the `CompositeDisposable` and wire up the `onCleared` function.
The natural extension onto this would be to create a base class for our
ViewModel which could look something like this…

```bash
abstract class DisposingViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }    override fun onCleared() {
        compositeDisposable.clear()
    }
}
```

Then in every ViewModel that needs this code it simply just extends from
the `DisposingViewModel` and it just has to invoke `addDisposable`. If
you are against the idea of inheritance being used in this way (you
aren’t alone) then it’s worth noting that creating a solution for this
<a
href="https://hackernoon.com/why-you-should-use-class-delegation-in-kotlin-fb0a3ebf151e"
class="z pl" rel="noopener ugc nofollow" target="_blank">using class
delegation in Kotlin</a> is not much more work.

## Is There An Even Better Way?

There might be. Whether it be a more home grown solution or a battle
tested open source project I’m sure something out there exists. At this
time I wanted to write a follow-up article so I could deprecate the
previous one that continues to rank well on Google and potentially
mislead developers. So more on that to come, in the mean time if you
have additional thoughts feel free to leave a comment.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

*Thanks for taking the time to read through my article. If you found
something to be not quite right or have other information to add please
reach out in the comments section below. If you enjoyed this article,
please click on the clap icon a few times or share it on social media
(or both). Lastly, I’m starting up a mailing list that is powered by*
<a href="https://www.activecampaign.com/" class="z pl"
rel="noopener ugc nofollow" target="_blank"><em>ActiveCampaign</em></a>*,
if you want to get weekly newsletters then*
<a href="https://upscri.be/fac601/" class="z pl"
rel="noopener ugc nofollow" target="_blank"><em>please use the sign-up
form located here</em></a>*.*

