---
title: "Using Components to Wire Everything Together in Dagger 2"
date: 2018-12-13
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# Using Components to Wire Everything Together in Dagger 2

Yesterday we learned about
<a href="/how-modules-work-in-dagger-2-643be6939b51" class="z pg"
rel="noopener ugc nofollow" target="_blank" data-discover="true">how
modules are used in Dagger 2</a> to tell the framework how it should
provide a dependency to a class that needs it. However it’s not enough
to just create many different modules and expect Dagger to just work. In
order to put everything together you need to create components which are
the glue that holds everything together. In this article we will discuss
how to use the `@Component` annotation in Dagger 2.

<figure class="pm pn po pp pq pl bd paragraph-image">
<img src="_media/785020_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a
href="https://unsplash.com/photos/pMW4jzELQCw?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z pg" rel="noopener ugc nofollow" target="_blank">Nathan
Dumlao</a> on <a
href="https://unsplash.com/search/photos/coffee?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z pg" rel="noopener ugc nofollow"
target="_blank">Unsplash</a></figcaption>
</figure>

## What Are Components In Dagger 2

Components are essentially the glue that holds everything together. They
are a way of telling Dagger 2 what dependencies should be bundled
together and made available to a given instance so they can be used.
They provide a way for a class to request dependencies being injected
through their `@Inject` annotation. If it helps to still call these
components magic then that’s fine too, they do a lot of heavy lifting
under the hood and it would be difficult to have a true grasp on what
they are without first using them yourself.

## How To Use Components In Dagger 2

Going back to the <a
href="https://github.com/CodyEngel/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di/AppComponent.kt"
class="z pg" rel="noopener ugc nofollow" target="_blank">Github Browser
Sample app</a> we can see that the `AppComponent.kt` is a fairly minimal
implementation which is great when you know how to use it, but not so
great when you are just starting out. Let’s start by looking at the code
necessary to make dependencies available to this component.

```bash
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class]
)
interface AppComponent {
    // More code, kind of confusing at first, etc, etc.
}
```

Going from top down we start out by saying this component should be a
singleton, so only one of these components should ever exist in memory
at a given time. Next we have the `@Component` annotation which allows
us to provide the modules we want to use, in our case we are providing
`AndroidInjectionModule`, `MainActivityModule`, and `AppModule` which we
discussed in our last article. While not something I plan to discuss in
depth, this annotation also supports the ability to pass in dependencies
by class as well using the `dependencies` function which takes an array
of classes.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

<img
src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMSIgaGVpZ2h0PSIxMSIgdmlld2JveD0iMCAwIDExIDExIiBjbGFzcz0ic3ciPjxwYXRoIGQ9Im0wIDYuMzEzIDMuNzA0IDMuNzA1LjkwNC45MDQuNjYtMS4wOTUgNS4yOTYtOC43OTVMOC44NSAwIDMuNTU0IDguNzk1bDEuNTYzLS4xOTEtMy43MDQtMy43MDV6IiAvPjwvc3ZnPg=="
class="sw" />

Remember me for faster sign in

Next we have to instruct the component how to build itself. We do this
with the `@Component.Builder` annotation.

```bash
@Component.Builder
interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
}
```

This will allow us to build the component by simply passing in an
instance of an `Application` which in our case is `GithubApp`. From
there the final part of this class is telling the component what classes
it can inject on. We do this by providing `inject` functions which look
like this **`fun`**` inject(githubApp: GithubApp)`. You will need to
provide a function for every class the component can inject on, however
thanks to an Android support library for Dagger 2 we don’t have to worry
about cluttering up our component either.

Now that we have the component setup there is still one more piece to
the puzzle. We need to actually call the `inject` function on the
component itself. The sample we are working off of can get a little
convoluted for the purposes of an article so I’ll just talk through the
bare minimum and save the intricacies of the `AndroidSupportInjection`
class for another time. At this point you would build your application
to allow the annotation processor to generate your component, once you
are done building you should have a `DaggerAppComponent` which can be
used. Every component that is generated will be prefixed with `Dagger`
to *hopefully* avoid name collisions.

```bash
DaggerAppComponent.builder()
    .application(githubApp)
    .build()
    .inject(githubApp)
```

It is at this point that we can finally inject our dependencies into the
class that requires this. By calling `inject` on the component it will
tell the framework to go through the class and inject everything with
the `@Inject` annotation. It will use the dependencies available to the
component as a way providing the fields or constructors with the
concrete classes they require.

That concludes the meat and potatoes portion of this tutorial. At this
point you should have a good idea of how the Dagger 2 mechanically
works. It’s fine if you still don’t feel truly comfortable with it just
as long as you have a baseline understanding and can start adding
dependencies yourself. In our next article I will go over how you can do
additional wiring of dependencies using the `AndroidSupportInjection`
class which unfortunately is the last puzzle piece required to have a
truly seamless Dagger 2 implementation.

Are there any parts of this article that should have explained the topic
further? I’d love to see what areas you found confusing so I can provide
updates to make this more useful.

*Thanks for taking the time to read through my article. If you enjoyed
this article, please click on the clap icon a few times or share it on
social media (or both). Lastly, I’m starting up a mailing list that is
powered by*
<a href="https://jobs.lever.co/activecampaign?lever-via=EzfHfUOaE0"
class="z pg" rel="noopener ugc nofollow"
target="_blank"><em>ActiveCampaign</em></a>*, if you want to get weekly
newsletters then please use the*
<a href="https://upscri.be/fac601/" class="z pg"
rel="noopener ugc nofollow" target="_blank"><em>sign-up form located
here</em></a>*.*

