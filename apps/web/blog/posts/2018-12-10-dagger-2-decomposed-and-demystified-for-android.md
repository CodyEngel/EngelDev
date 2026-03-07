---
title: "Dagger 2 Decomposed and Demystified for Android"
date: 2018-12-10
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# Dagger 2 Decomposed and Demystified for Android

When it comes to confusing and yet incredibly powerful frameworks,
Dagger 2 has to rank somewhere in the top five.What are components? What
are modules? What does that `@Inject` annotation do? How do I provide
dependencies to the other modules? Isn’t Dagger 2 just a lot of extra
work? I hope to answer all of these questions in a series of articles
aimed at teaching Dagger 2 in the way that made the most sense to me.
I’m happy to say that we have seen success using this dependency
injection framework at
<a href="https://www.activecampaign.com/" class="z pl"
rel="noopener ugc nofollow" target="_blank">ActiveCampaign</a> and I
hope to help others experience similar success too.

<figure class="pn po pp pq pr pm bd paragraph-image">
<img src="_media/967073_image1.jpg" loading="eager"
role="presentation" />
<figcaption>This article is not for fans of the infamous thermosiphon
example from the official Dagger 2 <a
href="https://google.github.io/dagger/users-guide" class="z pl"
rel="noopener ugc nofollow" target="_blank">user guide</a>. Photo via <a
href="https://unsplash.com/photos/NRZAwYyaYNk" class="z pl"
rel="noopener ugc nofollow" target="_blank">Daryan
Shamkhali</a>.</figcaption>
</figure>

## Why Use Dagger 2?

All of the annotations used in Dagger 2 are used to generate code for
your application to leverage. Since this is just code that is generated
for you it’s nothing that you couldn’t do yourself, so why use Dagger 2
at all? When faced with the steep learning curve this was often the
question I asked myself and swiftly answered by saying “Dagger 2 is
pointless, I’ll just do it myself.” After using it extensively over the
past six months though I’ve changed my mind completely.

Using a framework like Dagger 2 allows you to define the relationships
between your objects in a contained area. In smaller applications this
may seem trivial but as your application increases in size trying to
mange the dependencies will become a major chore. It is also useful that
you can define the scope of your dependencies to ensure they are only
kept around when necessary.

As your overall application you’ll find that the dependencies required
will change as well. If you are doing this yourself it can become a
major chore, allowing a framework to handle this for you makes life
infinitely easier (once you understand how to use it). One example of
this came up recently when my team decided to start adding performance
monitoring to our classes. We have a `Telemetry` interface which defines
the different operations that can be performed. Using Dagger 2 we just
say that our object needs an instance of `Telemetry` and then ensure we
add the necessary wiring with Dagger so it is able to provide that
dependency. This results in changing two classes (the object that needed
the dependency, and a `Telemetry` singleton to our dependency graph).

Once you are using Dagger 2 for a little while with success you should
also notice your overall system design will get better. You see, when
providing dependencies is difficult we find corners to cut to make it
easier. When you cut corners on anything it will usually catch up with
you someday, whether tomorrow or years later. In this case you’ll
usually find as soon as you try to test a class that is not loosely
coupled which favors composition over inheritance it will be nearly
impossible to test without cutting other corners.

## The Github Browser Sample App

Over the course of this series I will be leveraging the <a
href="https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample"
class="z pl" rel="noopener ugc nofollow" target="_blank">Github Browser
Sample app</a>. This sample app was built using the Android Architecture
Components along with Dagger 2 and serves as a great baseline for how to
build an app using both of those frameworks together.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

<img
src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMSIgaGVpZ2h0PSIxMSIgdmlld2JveD0iMCAwIDExIDExIiBjbGFzcz0ic28iPjxwYXRoIGQ9Im0wIDYuMzEzIDMuNzA0IDMuNzA1LjkwNC45MDQuNjYtMS4wOTUgNS4yOTYtOC43OTVMOC44NSAwIDMuNTU0IDguNzk1bDEuNTYzLS4xOTEtMy43MDQtMy43MDV6IiAvPjwvc3ZnPg=="
class="so" />

Remember me for faster sign in

My plan is to cover each area of Dagger 2 to make readers comfortable
with the framework. As I am still relatively new to using the framework
with success I don’t plan to cover things like Scoping in depth, I’ll
discuss it as much as necessary though. I also don’t plan to
intentionally cover what dependency injection is. In it’s simplest form
it’s just providing dependencies through constructor arguments, it’s
most complex form is Dagger 2.

## Keep Reading

Over the next few days I will be writing bite-sized articles focused on
specific areas are Dagger 2. My goal is to be able to focus on specific
areas of the framework so to ensure that nothing gets glossed over. As
articles in the series are published I will update this section with
links to the articles.

Part 1—
<a href="/dagger-2-decomposed-and-demystified-for-android-6a8ff6ad59c0"
class="z pl" rel="noopener ugc nofollow" target="_blank"
data-discover="true">Dagger 2 Decomposed and Demystified for Android</a>

Part 2—
<a href="/foundations-of-dagger-2-for-android-5ea1c14bceb1" class="z pl"
rel="noopener ugc nofollow" target="_blank"
data-discover="true">Foundations of Dagger 2 for Android</a>

Part 3—
<a href="/how-modules-work-in-dagger-2-643be6939b51" class="z pl"
rel="noopener ugc nofollow" target="_blank" data-discover="true">How
Modules Work in Dagger 2</a>

Part 4— <a
href="/using-components-to-wire-everything-together-in-dagger-2-8c2844e7f3c2"
class="z pl" rel="noopener ugc nofollow" target="_blank"
data-discover="true">Using Components to Wire Everything Together in
Dagger 2</a>

Part 5— Getting Your Dependencies From Dagger 2 in Android

What are you hoping to learn about Dagger 2? I’d love to see what areas
others find confusing so I can hopefully tailor future articles in the
series around those areas.

*Thanks for taking the time to read through my article. If you enjoyed
this article, please click on the clap icon a few times or share it on
social media (or both). Lastly, I’m starting up a mailing list that is
powered by*
<a href="https://jobs.lever.co/activecampaign?lever-via=EzfHfUOaE0"
class="z pl" rel="noopener ugc nofollow"
target="_blank"><em>ActiveCampaign</em></a>*, if you want to get weekly
newsletters then please use the*
<a href="https://upscri.be/fac601/" class="z pl"
rel="noopener ugc nofollow" target="_blank"><em>sign-up form located
here</em></a>*.*

