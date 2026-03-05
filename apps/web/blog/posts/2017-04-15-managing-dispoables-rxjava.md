---
title: Managing Disposables in RxJava 2 — For Android
date: 2017-04-15
description: Managing Disposables in RxJava 2 the wrong way.
tags:
  - android
  - java
  - software engineering
  - rxjava
  - reactive programming
---

![Thanks for this picture of disposable items, Unsplash.](../../assets/images/source/managing-disposables-android.webp)

_Update_: The method outlined below for managing disposables is rather misleading and not intended for use in production apps. While it serves a good purpose as a baseline of example of what you could do, it’s by no means copy + paste friendly. If you are looking for an updated version with a technique for managing disposables in a production friendly way then I strongly suggest reading [_Managing Disposables in RxJava2 — The Less Bad Version_](https://proandroiddev.com/managing-disposables-in-rxjava-2-the-less-bad-version-b3ff2b0b72a2).

We haven’t yet moved over to RxJava 2 at work so forgive me if I’m late to the game on this one. I recently started working more on side projects after work and with that came using RxJava 2. At work we use _CompositeSubscriptions_ for managing the subscriptions that are created so we can easily _unsubscribe_ from everything if the Android overlords decide to destroy our activity. To my surprise (because reading changelogs is not my thing) I could not find this _CompositeSubscription_ in my RxJava 2 projects, instead there was a _CompositeDisposable_. Also to my surprise my _Observer_ had a new callback…

## The New Callback

So it turns out _Observer_ has a callback which goes by the name of _onSubscribe_, and it passes back a _Disposable_ object. What this means (unless I’m totally doing things wrong) is you can take that _Disposable_ and throw it into a _CompositeDisposable_. What this also means is you can get rid of the boilerplate code required to add a _Disposable_ to a _CompositeDisposable_. Okay, it’s Friday and I don’t want to type much, so let’s just jump into what I came up with.

[**DisposingObserver**](https://gist.github.com/CodyEngel/37f366a984709f76bce3c9ee352b0f34)**.java**

Forget about using the regular Observer, use this one instead. Keep in mind the default implementations are left blank because you’ll have to call _new DisposingObserver(…)_ instead of letting lambdas make things prettier.

```java
public class DisposingObserver<T> implements Observer<T> {
    @Override
    @CallSuper
    public void onSubscribe(Disposable d) {
        DisposableManager.add(d);
    }
    @Override
    public void onNext(T next) {}
    @Override
    public void onError(Throwable e) {}
    @Override
    public void onComplete() {}
}
```

[**DisposableManager**](https://gist.github.com/CodyEngel/348d4e0ec457ac071839404e7d1cf99a)**.java**

You may have noticed that _onSubscribe_ calls this class but I unfortunately did not introduce it first, as the junior engineers say, ¯\_(ツ)_/¯

```java
public class DisposableManager {
    private static CompositeDisposable compositeDisposable;
    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }
    public static void dispose() {
        getCompositeDisposable().dispose();
    }
    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return _compositeDisposable_;
    }
    private DisposableManager() {}
}
```

You may have noticed that I’m also checking if _compositeDisposable.isDisposed()_ is true, and if it is I create a new one. The reason for this is when you call _dispose()_ that _CompositeDisposable_ may as well no longer exist (as in, don’t add anything else to it because you’ll just be disappointed and frustrated when nothing works).

**SuperAwesomeActivity.java**

Okay so at this point I am going to show you what happens in your Activity (or I suppose Fragment, View, or whatever else you want to tie this to).

```java
@Override
protected void onDestroy() {
    DisposableManager.dispose();
    super.onDestroy();
}
```

So let’s summarize what we’ve just seen. You have a _DisposingObserver_ which will automatically add itself to a _CompositeDisposable_. Your _CompositeDisposable_ can be accessed anywhere in your app through the _DisposableManager_ which allows you then _dispose_ all of your _Disposables_ anywhere you want. For my purposes I do this in _onDestroy_ but you can do it anywhere you want.