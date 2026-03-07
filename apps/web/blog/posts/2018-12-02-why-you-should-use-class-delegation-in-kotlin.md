---
title: "Why You Should Use Class Delegation in Kotlin"
date: 2018-12-02
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# Why You Should Use Class Delegation In Kotlin

In most traditional programming languages code reuse typically comes in
the form of inheritance. I’ve been on many projects where the innocent
`BaseThingy` quickly turns into `DoesEveryThingy` in just a few short
months because unfortunately inheritance wasn’t meant for code reuse in
ways we often hope for. You see, well designed systems are often
constructed from smaller objects that do one thing well and leave
everything else to others. The problem is sometimes we have classes that
require management of other smaller objects, we can call these classes
`Controller`s as their job is to control other classes. It can be
tempting to create a `BaseController` which will handle things like
thread pool management, instantiating different views, injecting models,
etc. The downside to this approach comes when you have a lot of child
controllers relying on the base implementation except they need to
manage the thread pool slightly differently or maybe you want to inject
the models using a different approach, or maybe you only need one part
of the base class and nothing more. There has to be a better way, this
is where the idea of <a
href="https://robots.thoughtbot.com/reusable-oo-composition-vs-inheritance"
class="z pl" rel="noopener ugc nofollow" target="_blank">composition
over inheritance</a> comes into play.

<figure class="pn po pp pq pr pm bd paragraph-image">
<img src="_media/766123_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a href="https://unsplash.com/photos/geNNFqfvw48"
class="z pl" rel="noopener ugc nofollow" target="_blank">Michał
Parzuchowski</a></figcaption>
</figure>

## Composition Is Hard Though!

One of the downsides of composition with something like Java is the
language itself doesn’t give you many tools to break down the tedium of
using the pattern. For example, let’s say we want to manage disposables
in RxJava, favoring composition over inheritance we’d create an
interface called `DisposableHandler` which could look something like
this…

```bash
public interface DisposableHandler {    public void addDisposable(Disposable disposable);

    public void addDisposables(List<Disposable> disposables);

    public void clearDisposables();
}
```

From there we will want to define a class that can implement our
`DisposableHandler` interface which can be used to delegate the calls
from our larger Controller to logic that can be reused easily. In our
example we’ll use a `CompositeDisposable` for handling collections of
disposables, so we might end up with something like this…

```bash
public class CompositeDisposableHandler
    implements DisposableHandler {

    private final List<Disposable> disposableList 
        = new ArrayList<>();

    @Override
    public void addDisposable(Disposable disposable) {
        disposableList.add(disposable);
    }

    @Override
    public void addDisposables(List<Disposable> disposables) {
        disposableList.addAll(disposables);
    }

    @Override
    public void clearDisposables() {
        for (Disposable disposable : disposableList) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        disposableList.clear();
    }
}
```

Okay we’re almost done with our composition over inheritance solution at
this point, all we need now is our controller which will proxy all of
our calls into our `CompositeDisposableHandler` as opposed to
implementing the logic itself. Our `SuperSimpleController` will look
something like this…

```bash
public class SuperSimpleController
    implements DisposableHandler {

    private final CompositeDisposableHandler
        compositeDisposableHandler 
            = new CompositeDisposableHandler();

    @Override
    public void addDisposable(Disposable disposable) {
        compositeDisposableHandler.add(disposable);
    }

    @Override
    public void addDisposables(List<Disposable> disposables) {
        compositeDisposableHandler.addDisposables(disposables);
    }

    @Override
    public void clearDisposables() {
        compositeDisposableHandler.clearDisposables();
    }
}
```

All of this is fine until you have to implement the next controller, or
the controller after that. You see, everything in
`SuperSimpleController` is just copy and paste code. This my friends is
how you start off with composition and eventually rename
`SuperSimpleController` into `BaseController` which will turn into an
unwieldy god class. This is why I think most of the Java code I see
relies so heavily on inheritance, not because the developers aren’t
aware of composition but because we are lazy and boilerplate sucks.
There has to be a better way.

## Composition Is Easy In Kotlin!

If you are using Kotlin instead of Java (and if you aren’t what’s wrong
with you?) then the language actually offers easy composition out of the
box, the feature is called
<a href="https://kotlinlang.org/docs/reference/delegation.html"
class="z pl" rel="noopener ugc nofollow" target="_blank">delegation</a>.
So let’s dive right in and see what our Java code looks like when
translated into Kotlin.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

Our interface definition for `DisposableHandler` remains largely the
same with the exception of some keywords changing or disappearing
entirely (gotta love how concise Kotlin is).

```bash
interface DisposableHandler {

    fun addDisposable(disposable: Disposable)

    fun addDisposables(disposables: List<Disposable>)

    fun clearDisposables()
}
```

From here we can now create our `CompositeDisposableHandler`, which
again will look familiar outside of some changes for Kotlin.

```bash
class CompositeDisposableHandler : DisposableHandler {
    
    private val disposableList = mutableListOf<Disposable>()
    
    override fun addDisposable(disposable: Disposable) {
        disposableList.add(disposable)
    }

    override fun addDisposables(disposables: List<Disposable>) {
        disposableList.addAll(disposables)
    }

    override fun clearDisposables() {
        disposableList.forEach { disposable -> 
            if (!disposable.isDisposed) {
                dispoable.dispose()
            }
        }
        disposableList.clear()
    }
}
```

Finally it’s time for our `SuperSimpleController` which required a lot
of boilerplate in Java. Thanks to Kotlin our implementation of this
class can be shown one a single line (broken up to two lines for
readability on Medium). If you are a RxJava snob you may have noticed
that we aren’t using `CompositeDisposable` in this example, that’s
because this is an example article trying to describe a use case, your
actual implementation may vary.

```bash
class SuperSimpleController 
    : DisposableHandler by CompositeDisposableHandler()
```

## How Does Class Delegation Work In Kotlin?

<figure class="pn po pp pq pr pm pv pw paragraph-image">

<img src="_media/766123_image2.jpg" loading="lazy"
role="presentation" />

<figcaption>If you’re the type that settles for a gif over a real
explanation then this is the end of the article…</figcaption>
</figure>

…For everyone else let’s dig a little deeper (but now much deeper).
Class delegation in Kotlin works through the aptly named `by` keyword.
If you have a class that needs to implement an interface but you’d like
to reuse the implementation of another class then simply say
`Interface by ClassInstanceThatAlreadyImplementsTheInterface` and you’re
done, no need for pointless `override` keywords cluttering up your code.

In my own usage of class delegation I tend to create a class which just
implements the interface I’ve declared and then in the larger
`Controller` style classes I will just say they implement an interface
and use the instance of the `by` keyword. At the end of the day this is
just doing what the Java code at the start of this article did, it just
does it a slightly more elegant way.

*Thanks for taking the time to read through my article. If you found
something to be not quite right or have other information to add please
reach out in the comments section below. If you enjoyed this article,
please click on the clap icon a few times or share it on social media
(or both). Lastly, I’m starting up a mailing list that is powered by*
<a href="https://www.activecampaign.com/" class="z pl"
rel="noopener ugc nofollow" target="_blank"><em>ActiveCampaign</em></a>*,
if you want to get weekly newsletters then please use the sign-up form
below.*

<figure class="pn po pp pq pr pm">

</figure>

