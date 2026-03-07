---
title: "Managing Disposables in RxJava 2 — The Less Bad Version"
date: 2018-12-06
description: A follow-up to an earlier article on RxJava disposables, offering better scoping strategies using CompositeDisposable per controller.
tags:
  - android
  - rxjava
  - kotlin
---

I wrote an article about managing disposables back in 2017. This was my first stab at trying to work with the new callbacks and wanting to create a boiler-plate free way to work with them. I made a rather glaring mistake though, the entire fictitious application has a single `CompositeDisposable` which means when one controller is destroyed it would try `dispose` which will prematurely dispose all of your streams. Yikes, that's definitely not a good idea. The article spawned a lot of great discussion and I didn't see much reason to provide an update, if you check the comments section then you'll quickly know it's a bad idea after-all. Unfortunately that article started ranking really well on Google (it's now number one for the query *managing disposables* or *composite disposable* and is one of my most viewed articles); it's time to provide alternative solutions.

![Photo by Bas Emmen](../../assets/images/source/2018-12-06-managing-disposables-in-rxjava-2-the-less-bad-version-1.jpg "Photo by Bas Emmen")

## Scoping Disposables Is Important

Unless you want bad things to happen it's important that your disposables aren't globally scoped and you can dispose of some without disposing of everything. In the Android world I've found that having one `CompositeDisposable` per view controller (`Activity`/`Fragment`) as well as one per `ViewModel` seems to work out fairly well. This means we want something that looks something like this…

```kotlin
class ExampleViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun doSomething() {
        val disposable = Single.just(1)
            .subscribe {
                //update something onNext
            }
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}
```

This will work out fine however in every ViewModel we are forced to create the `CompositeDisposable` and wire up the `onCleared` function. The natural extension onto this would be to create a base class for our ViewModel which could look something like this…

```kotlin
abstract class DisposingViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}
```

Then in every ViewModel that needs this code it simply just extends from the `DisposingViewModel` and it just has to invoke `addDisposable`. If you are against the idea of inheritance being used in this way (you aren't alone) then it's worth noting that creating a solution for this [using class delegation in Kotlin](https://hackernoon.com/why-you-should-use-class-delegation-in-kotlin-fb0a3ebf151e) is not much more work.

## Is There An Even Better Way?

There might be. Whether it be a more home grown solution or a battle tested open source project I'm sure something out there exists. At this time I wanted to write a follow-up article so I could deprecate the previous one that continues to rank well on Google and potentially mislead developers. So more on that to come, in the mean time if you have additional thoughts feel free to share it with friends or colleagues.