---
title: "Foundations of Dagger 2 for Android"
date: 2018-12-11
description: Part 2 of a Dagger 2 series — how @Inject works for field and constructor injection in Android, with real GithubBrowserSample examples.
tags:
  - android
  - kotlin
  - dagger
  - dependency-injection
---

When I was first learning Dagger 2 I focused on the main parts of the framework as opposed to areas I'd be interacting with most often. This meant I spent a lot of time reading about modules and components but fairly little time learning about how I actually provide those dependencies to the classes I'd be invoking in my code. I think that was more detrimental than beneficial and I want to start off by discussing what Dagger 2 looks like in your day to day code; we'll ignore modules and components for this article. Keep in mind that all of the code I use for this article will come from the [GithubBrowserSample by Google](https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample), [forked by me](https://github.com/CodyEngel/android-architecture-components/tree/master/GithubBrowserSample) in case they ever take it down in the future.

![Photo by Nathan Dumlao on Unsplash](../../assets/images/source/2018-12-11-foundations-of-dagger-2-for-android-1.jpg "Photo by Nathan Dumlao on Unsplash")

## Dagger 2 Builds Your Dependency Graph

We will talk about this in more depth with future articles however it will be hard to discuss the topics in this article without at least glossing over this idea. The way I look at Dagger is it is a tool that builds the dependency graph for me. If you are providing the dependencies yourself then it can become difficult to see where they are actually coming from because you're just providing a `new` instance of a class *somewhere* in your application. With Dagger it is explicitly defined through your modules and components.

Whenever Dagger needs to provide a dependency it will traverse it's graph within that scope to see if it already has an instance of that object it can use. If it doesn't find that then it will create a new instance and provide that instead. When I mention the dependency graph in the future, this is what I am talking about.

## Letting Dagger 2 Provide The Dependencies

When it comes to actually using your dependencies in Dagger 2 you'll rely heavily on the `@Inject` annotation. This annotation lets the framework know that you aren't going to supply the dependency and you expect it to come from the dependency graph. So remember, anytime you want Dagger to provide the dependency you want to mark it with that annotation.

### Field Injection In Action

Looking at the `UserFragment.kt` file in the GithubBrowserSample provides an excellent example of Dagger 2 field injection in action. You should use this method whenever you don't have direct access to the constructor. In an Android app this typically means using it for framework classes such as views, activities, fragments, or the application class.

```kotlin
class UserFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors
```

Looking at the class itself we'll see that our `ViewModelProvider.Factory` is a lateinit var with the Inject annotation. In Kotlin you need to mark these as lateinit since they will be initialized by Dagger at a later point in time. We also mark the `AppExecutors` with the Inject annotation. This signals to Dagger 2 that it should find an instance of that dependency (or create one if it doesn't find one) and provide it to us. Outside of some additional setup code (again, we'll discuss that in a future article) this is good to use.

### Constructor Injection In Action

This is the preferred way to provide dependencies not only in Dagger 2 but in general as well. I feel like this is also where things get a little confusing because it was never clear to me how they were used or how it worked. Let's take a look at the `UserRepository.kt` as it is an excellent example of constructor injection.

```kotlin
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
)
```

In Kotlin this looks a little verbose since we have to manually say `constructor` instead if it just being inferred. This is because we need to tag it with the Inject annotation. What this code is doing is telling Dagger that it should find instances of `AppExectors`, `UserDao`, and `GithubService` somewhere in it's graph or create new instances of them. Not only that but it will also add this to the dependency graph. With `UserRepository` added to our graph it means we can have it injected via field injection or through constructor injection somewhere else. In this case we are injecting `UserRepository` into the `UserViewModel`'s constructor which is also being injected.

## This Seems Like Magic

For now it's okay to write it off as magic. I hope by the end of this series it won't seem as magical anymore, but for the purpose of getting started I think it's good to call things magic. If you aren't satisfied with that answer then I'll point to the `GithubViewModelFactory` class.

```kotlin
@Singleton
class GithubViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
```

It's okay to be confused, I plan to explain this in-depth in a later article. For now the only thing you need to know is this is what allows us to inject our ViewModel classes in Android, and it is probably one of the most important take aways from this series.

Are there any parts of this article that could be explained more clearly? If this series has been helpful so far, feel free to share it with friends or colleagues.