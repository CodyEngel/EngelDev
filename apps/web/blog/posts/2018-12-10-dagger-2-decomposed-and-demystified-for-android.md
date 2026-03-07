---
title: "Dagger 2 Decomposed and Demystified for Android"
date: 2018-12-10
description: An introduction to Dagger 2 for Android, explaining what it is and why it's worth learning, with real code from the GithubBrowserSample.
tags:
  - android
  - kotlin
  - dagger
  - dependency-injection
---

When it comes to confusing and yet incredibly powerful frameworks, Dagger 2 has to rank somewhere in the top five. What are components? What are modules? What does that `@Inject` annotation do? How do I provide dependencies to the other modules? Isn't Dagger 2 just a lot of extra work? I hope to answer all of these questions in a series of articles aimed at teaching Dagger 2 in the way that made the most sense to me. I'm happy to say that we have seen success using this dependency injection framework at [ActiveCampaign](https://www.activecampaign.com/) and I hope to help others experience similar success too.

![This article is not for fans of the infamous thermosiphon example from the official Dagger 2 user guide. Photo via Daryan Shamkhali.](../../assets/images/source/2018-12-10-dagger-2-decomposed-and-demystified-for-android-1.jpg "This article is not for fans of the infamous thermosiphon example from the official Dagger 2 user guide. Photo via Daryan Shamkhali.")

## Why Use Dagger 2?

All of the annotations used in Dagger 2 are used to generate code for your application to leverage. Since this is just code that is generated for you it's nothing that you couldn't do yourself, so why use Dagger 2 at all? When faced with the steep learning curve this was often the question I asked myself and swiftly answered by saying "Dagger 2 is pointless, I'll just do it myself." After using it extensively over the past six months though I've changed my mind completely.

Using a framework like Dagger 2 allows you to define the relationships between your objects in a contained area. In smaller applications this may seem trivial but as your application increases in size trying to mange the dependencies will become a major chore. It is also useful that you can define the scope of your dependencies to ensure they are only kept around when necessary.

As your overall application you'll find that the dependencies required will change as well. If you are doing this yourself it can become a major chore, allowing a framework to handle this for you makes life infinitely easier (once you understand how to use it). One example of this came up recently when my team decided to start adding performance monitoring to our classes. We have a `Telemetry` interface which defines the different operations that can be performed. Using Dagger 2 we just say that our object needs an instance of `Telemetry` and then ensure we add the necessary wiring with Dagger so it is able to provide that dependency. This results in changing two classes (the object that needed the dependency, and a `Telemetry` singleton to our dependency graph).

Once you are using Dagger 2 for a little while with success you should also notice your overall system design will get better. You see, when providing dependencies is difficult we find corners to cut to make it easier. When you cut corners on anything it will usually catch up with you someday, whether tomorrow or years later. In this case you'll usually find as soon as you try to test a class that is not loosely coupled which favors composition over inheritance it will be nearly impossible to test without cutting other corners.

## The Github Browser Sample App

Over the course of this series I will be leveraging the [Github Browser Sample app](https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample). This sample app was built using the Android Architecture Components along with Dagger 2 and serves as a great baseline for how to build an app using both of those frameworks together.

My plan is to cover each area of Dagger 2 to make readers comfortable with the framework. As I am still relatively new to using the framework with success I don't plan to cover things like Scoping in depth, I'll discuss it as much as necessary though. I also don't plan to intentionally cover what dependency injection is. In it's simplest form it's just providing dependencies through constructor arguments, it's most complex form is Dagger 2.

## Keep Reading

Over the next few days I will be writing bite-sized articles focused on specific areas of Dagger 2. My goal is to be able to focus on specific areas of the framework to ensure that nothing gets glossed over. As articles in the series are published I will update this section with links to the articles.

Part 1 — Dagger 2 Decomposed and Demystified for Android

Part 2 — Foundations of Dagger 2 for Android

Part 3 — How Modules Work in Dagger 2

Part 4 — Using Components to Wire Everything Together in Dagger 2

Part 5 — Getting Your Dependencies From Dagger 2 in Android

Hopefully this series helps make Dagger 2 less intimidating. If you found it useful, feel free to share it with friends or colleagues.