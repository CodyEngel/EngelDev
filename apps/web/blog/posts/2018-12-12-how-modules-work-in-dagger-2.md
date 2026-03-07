---
title: "How Modules Work in Dagger 2"
date: 2018-12-12
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# How Modules Work in Dagger 2

Yesterday we discussed
<a href="/foundations-of-dagger-2-for-android-5ea1c14bceb1" class="z ph"
rel="noopener ugc nofollow" target="_blank" data-discover="true">the
foundations of Dagger 2 for Android</a>. That article outlined the
importance of the `@Inject` annotation and eluded to how this dependency
injection framework would hook into those annotations. The purpose of
this article is to explain the why and how of the `@Module` annotation
in Dagger.

<figure class="pn po pp pq pr pm bd paragraph-image">
<img src="_media/178363_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a
href="https://unsplash.com/photos/obV_LM0KjxY?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow" target="_blank">Tina Guina</a>
on <a
href="https://unsplash.com/search/photos/coffee?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow"
target="_blank">Unsplash</a></figcaption>
</figure>

## Why Modules Are Important In Dagger 2

Modules are a way of telling Dagger how to provide dependencies from the
dependency graph. These are typically high level dependencies that you
aren’t already contributing to the dependency graph through the
`@Inject` constructor annotation we discussed in our previous article.
This is also how Dagger allows us to compose our required dependencies
as you’ll see in tomorrow’s article about Components. Modules by
themselves don’t really do much, they just tell the framework how to
create the dependencies but they don’t allow you to use those
dependencies on their own (again, we’ll talk about how to do that
tomorrow). As you delve deeper into using this framework though you will
start to see the brilliance in this design choice.

## How To Setup Modules In Dagger 2

Modules in Dagger 2 are defined as classes with a `@Module` annotation.
Going back to the Github Browser Sample we are going to look at what the
<a
href="https://github.com/CodyEngel/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di/AppModule.kt"
class="z ph" rel="noopener ugc nofollow" target="_blank">AppModule</a>
looks like.

```bash
@Module
class AppModule {
    // ...
}
```

Above is our template for creating our first module which removes an
`includes` parameter you may see if you’re looking at the entire
AppModule in the sample app (this is to simplify the topic as that
parameter isn’t really necessary to start using this tool on your own).
From here we can start writing our functions which will tell Dagger how
to find and provide certain dependencies.

```bash
@Singleton
@Provides
fun provideGithubService(): GithubService {
    return Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()
        .create(GithubService::class.java)
}
```

The easiest one is also at the top of our sample module. This function
is denoted by the `@Provides` annotation which tells Dagger that it
should use this function when providing a `GithubService` dependency.
The framework is essentially looking for the annotation followed by the
return type to determine what is providing. The function name
`provideGithubService()` is not important it could be named
`giveMeChipotle` and still work the same; this naming convention does
make it easier to find out if you are already providing a particular
dependency though.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

<img
src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMSIgaGVpZ2h0PSIxMSIgdmlld2JveD0iMCAwIDExIDExIiBjbGFzcz0ic3kiPjxwYXRoIGQ9Im0wIDYuMzEzIDMuNzA0IDMuNzA1LjkwNC45MDQuNjYtMS4wOTUgNS4yOTYtOC43OTVMOC44NSAwIDMuNTU0IDguNzk1bDEuNTYzLS4xOTEtMy43MDQtMy43MDV6IiAvPjwvc3ZnPg=="
class="sy" />

Remember me for faster sign in

Inside of the function we write the code necessary to create an instance
of that dependency. In our case it’s just the code required to
instantiate a Retrofit builder followed by creating the `GithubService`
that will use the builder.

Outside of the function we have a `@Singleton` annotation which is part
of the javax inject library. While this isn’t part of Dagger 2
specifically, the framework will use that annotation to generate
additional boilerplate code that ensures we only ever create one
instance of our `GithubService` during our application’s lifecycle.

If you remember yesterday’s example of the `UserRepository` class we
were injecting a `GithubService` instance through the constructor, the
code above is the instance that is being provided. So instead of having
to write all of the code that would be necessary to manually do this
yourself, you just provide a few annotations and Dagger takes care of
connecting the dots for you.

```bash
@Singleton
@Provides
fun provideDb(app: Application): GithubDb {
    return Room
        .databaseBuilder(app, GithubDb::class.java, "github.db")
        .fallbackToDestructiveMigration()
        .build()
}

@Singleton
@Provides
fun provideUserDao(db: GithubDb): UserDao {
    return db.userDao()
}
```

This example is to show that you can pass in instances to your provide
functions. In our case our `provideDb` function requires an instance of
`Application` to create our Room database. So long as Dagger is able to
find that dependency somewhere in its graph then it will be able to pass
that into the function. In this case our `Application` ends up being at
the root of our dependency graph so there is no need to include it in
the module.

The next function, `provideUserDao` instructs Dagger that it requires a
`GithubDb` instance in order to provide a `UserDao` instance. We can
trust that the framework will handle this for us and just call the `db`
variable as if we created it ourselves. From there it’s as simple as
calling `db.userDao()` to access that instance. Now our `UserRepository`
from yesterday has all of the dependencies it needs in the dependency
graph and Dagger will be able to create that instance for us.

One caveat about this is you cannot have dependencies that depend on
each other in your graph. This creates a chicken or egg scenario where
Dagger doesn’t know how to provide `A` without creating `B` but in order
to create `B` it needs `A`. If you encounter this situation your
application will fail to build so you have the reassurance this won’t be
a runtime problem, however the build message isn’t always the easiest to
decrypt so just keep this in mind.

Are there any parts of this article that should have explained the topic
further? I’d love to see what areas you found confusing so I can provide
updates to make this more useful.

*Thanks for taking the time to read through my article. If you enjoyed
this article, please click on the clap icon a few times or share it on
social media (or both). Lastly, I’m starting up a mailing list that is
powered by*
<a href="https://jobs.lever.co/activecampaign?lever-via=EzfHfUOaE0"
class="z ph" rel="noopener ugc nofollow"
target="_blank"><em>ActiveCampaign</em></a>*, if you want to get weekly
newsletters then please use the*
<a href="https://upscri.be/fac601/" class="z ph"
rel="noopener ugc nofollow" target="_blank"><em>sign-up form located
here</em></a>*.*

