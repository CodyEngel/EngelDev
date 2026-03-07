---
title: "Getting Your Dependencies From Dagger 2 in Android"
date: 2018-12-14
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# Getting Your Dependencies From Dagger 2 in Android

In this series we’ve so far discussed the
<a href="/foundations-of-dagger-2-for-android-5ea1c14bceb1" class="z ph"
rel="noopener ugc nofollow" target="_blank"
data-discover="true">fundamentals of Dagger 2</a>,
<a href="/how-modules-work-in-dagger-2-643be6939b51" class="z ph"
rel="noopener ugc nofollow" target="_blank" data-discover="true">how to
create modules</a>, and <a
href="/using-components-to-wire-everything-together-in-dagger-2-8c2844e7f3c2"
class="z ph" rel="noopener ugc nofollow" target="_blank"
data-discover="true">what components are used for</a>. I wanted to
conclude this series by talking about how to tie all of this together
into your current (or next) Android application. While this article is
technically optional, I feel it actually ends up being the most
important piece because it drastically cuts down on the amount of
boilerplate required. With that, let’s get started.

<figure class="pj pk pl pm pn pi bd paragraph-image">
<img src="_media/874423_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a
href="https://unsplash.com/photos/yGb2igKldYg?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow" target="_blank">Tyler Nix</a>
on <a
href="https://unsplash.com/search/photos/coffee?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow"
target="_blank">Unsplash</a></figcaption>
</figure>

## Injecting Components Into Your Activity And Fragment

It can be a little strange when you finally need to start supplying
components to the main entry-point of your application (the activity or
fragment). While I haven’t seen this called out explicitly in most
articles I’ve read, looking at the <a
href="https://github.com/CodyEngel/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di/AppInjector.kt"
class="z ph" rel="noopener ugc nofollow" target="_blank">Github Browser
Sample</a> gives an almost copy & paste ready version of all the setup
you’ll ever need for your application. Let’s take a look at the
`AppInjector` class.

```bash
object AppInjector {
    fun init(githubApp: GithubApp) {
        DaggerAppComponent.builder().application(githubApp)
            .build().inject(githubApp)
```

The main public facing function is simply called `init` which will take
the `GithubApp` instance and build the generated DaggerAppComponent.
Once that is created we register activity lifecycle callbacks which will
notify the application whenever certain lifecycle events occur.

```bash
githubApp.registerActivityLifecycleCallbacks(object :
    Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(a: Activity, b: Bundle?) {
            handleActivity(a)
        }
        // ... the other overrides can just be empty
    })
```

Everytime an `Activity` calls `onCreate` the above callback will be
invoked. Which leads to the next question to answer, what does
`handleActivity` do?

```bash
private fun handleActivity(activity: Activity) {
    if (activity is HasSupportFragmentInjector) {
        AndroidInjection.inject(activity)
    }
    if (activity is FragmentActivity) {
        activity.supportFragmentManager
            .registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (f is Injectable) {
                            AndroidSupportInjection.inject(f)
                        }
                    }
                }, true
            )
    }
}
```

It does quite a bit, but the top level logic is just checking if the
activity is of certain types, `HasSupportFragmentInjector` is part of
the `dagger.android.support` package, while `FragmentActivity` is just
part of the framework (it’s the parent of `AppCompatActivity` so you
probably already use this anyway). The first if statement just invokes
`AndroidInjection.inject` which is part of `dagger.android` and handles
the injections from your app component for you. The next part is for
doing injections with Fragments which is largely following the same
logic we already discussed.

If this is confusing, please leave a comment below and feel free to just
copy and paste the `AppInjector` code for now. I think it’s perfectly
reasonable to chalk this up to *well it works, I’ll figure out the nitty
gritty when I need to*. Just don’t forget to actually call
`AppInjector.inject(...)` in your application’s `onCreate` function.

How does the framework know how to inject all of this? If you just try
to run the app right now it will fail to compile. In your application
class you need to have it implement `HasActivityInjector`, which from
there will require the following code…

```bash
@Inject
lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>override fun activityInjector() = dispatchingAndroidInjector
```

The first line is us just injecting the `DispatchingAndroidInjector`,
this doesn’t have to be defined in any module or component, Dagger can
just figure it out on it’s own. The second part is overriding
`activityInjector` to just return the field we are injecting.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

<img
src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMSIgaGVpZ2h0PSIxMSIgdmlld2JveD0iMCAwIDExIDExIiBjbGFzcz0ic3oiPjxwYXRoIGQ9Im0wIDYuMzEzIDMuNzA0IDMuNzA1LjkwNC45MDQuNjYtMS4wOTUgNS4yOTYtOC43OTVMOC44NSAwIDMuNTU0IDguNzk1bDEuNTYzLS4xOTEtMy43MDQtMy43MDV6IiAvPjwvc3ZnPg=="
class="sz" />

Remember me for faster sign in

The last piece to this puzzle involves one more module, and one more
annotation. The annotation is `@ContributesAndroidInjector` which just
says that this dependency should part of the dependency graph and it
will be used alongside the `dagger.android` classes.

```bash
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}
```

That’s all you have to worry about from the non-architecture components
world. If you are still unsure on how everything is working together I
strongly suggest pulling down the <a
href="https://github.com/CodyEngel/android-architecture-components/tree/master/GithubBrowserSample"
class="z ph" rel="noopener ugc nofollow" target="_blank">Github Browser
Sample</a> and play around with it a little to further familiarize
yourself with Dagger 2.

## What About Injecting Dependencies Into ViewModels

This part is admittedly a bit more involved and in many ways a lot more
*just accept that it works and don’t ask too many questions*. I’ll walk
through the mechanical setup and if you are curious on the nitty gritty
then let me know and I can follow-up with an article in the future.

We first need to create an annotation that can be used. In our case
we’ll call this `ViewModelKey` and it is responsible for building out a
key value pair that Dagger can use.

```bash
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
```

In our case the ViewModel class is the key, and that’s really the only
takeaway for this code snippet. The next part is creating the actual
ViewModelModule that can provide the ViewModels with all of the
dependencies necessary.

```bash
@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(
        factory: GithubViewModelFactory
    ): ViewModelProvider.Factory    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(
        userViewModel: UserViewModel
    ): ViewModel
}
```

This is slightly trimmed down from the actual version as the actual one
just has more ViewModels. We need to first create the
`bindViewModelFactory` function which will take a
`GitHubViewModelFactory` and will essentially handle how we create
ViewModels. The second part is the `bindUserViewModel` which has several
annotations which essentially tell Dagger to bind the ViewModel using
the `bindViewModelFactory` and store it inside of a map using the
`ViewModelKey` provided (which is the class itself). From there we need
to provide the function with the actual `ViewModel` we want, in our case
it’s the `UserViewModel` and then it should return a `ViewModel`.

Now you’re probably asking yourself, what does the Factory look like? It
looks kind of rough, but it works and you can’t really complain about
working code, right?

```bash
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

This factory figures out what type of ViewModel it should try to create.
If it’s unable to figure out how to do that it will thrown an exception
saying the model class was unknown. From there you just have to trust
that it works, at this point in time I can’t really think of a
simplified way to explain how it’s working, so you’ll just have to trust
me on this one.

The final part of all of this is to tell Dagger 2 that you want it to
inject the ViewModel’s constructor. If you don’t remember from our
earlier article where we discussed constructor injection, this is what
our `UserViewModel` looks like.

```bash
@Inject constructor(
    userRepository: UserRepository,
    repoRepository: RepoRepository
) : ViewModel() {
    // ...
}
```

That’s it. Since the `ViewModel` is defined in a module that we are
bundling as a component and supplying through our `AppInjector` all of
this is wired up and works. It may still seem like magic, and I think
that’s okay. Dagger 2 is kind of like magic, except you don’t even know
what trick is being performed, hopefully after this series you at least
know what the trick is and have a hunch as to how it’s working.

I hope this series was beneficial to everyone reading it. This is part
of my <a
href="https://medium.com/@CodyEngel/why-im-writing-31-articles-in-31-days-this-december-755ccd9b27e3"
class="z ph" rel="noopener">31 articles in 31 days challenge</a> and it
just wasn’t feasible to write this entire guide in one day, as a single
article. Some parts of this series may have felt a little rushed so
please let me know if you need further examples or explanations. I hope
to follow-up series sometime in the future with a FAQ to dig deeper into
the questions you have.

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

