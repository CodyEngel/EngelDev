---
title: Flax for Android — A Reactive Concept
date: 2017-04-17
description: FLAX is a new architecture pattern for Android which focuses on the concept of unidirectional data flows.
tags:
  - flax
  - android
  - architecture
  - unidirectional data flow
  - mvi
  - software engineering
  - java
---

![Photo by Noah Rosenfield via Unsplash](../../assets/images/source/flax-for-android.webp)
_Photo by Noah Rosenfield via Unsplash_


Earlier this month I decided to try out [Model-View-Intent](/blog/lets-try-mvi-on-android/) (MVI), and overall I enjoyed the idea behind it. More and more Android apps are using Reactive Extensions to handle concurrency and simplify event handling, and I think our architecture patterns need to shift away from MVP (Model View Presenter) to something similar to MVI. Flax is what I think makes sense for Rx based apps.

## Why Flax?

So after trying out MVI and see how much cleaner things looked I wanted to start looking at other design patterns that were out there. I quickly landed on [Flux](https://facebook.github.io/flux/) which is something another engineer at my company gave a talk on late last year. The unidirectional path for data to flow made sense and the added layers between the view and model seemed necessary to keep things clean. So I decided to base my implementation off of Flux. Flux for Android, it made sense to just call it Flax (plus I think it annoys my manager when I yell out “that’d be easier if we just used FLAX”).

## The Parts

The parts that make up a Flax application are as follows: Activity (or whatever you use), View, Responder, Store, and Renderer. I’ll go into what each one does in a moment but first I want to discuss how these are put together and how they interact with one another.

The Activity (or Fragment, Composite View, ViewController, or whatever) is how we interact with the Android sytem. This will instantiate our View, Responder, and Renderer objects. Actions from the View and system get sent to the Responder where it can then forward those onto our Store. The Store can update our Model and then notify the Renderer of changes. The Renderer will then take the current state of the Model and update the View.

## The Activity (Or Whatever You Use)

This is how we can actually interact with Android and setup various requirements. In it’s current state, an Activity using Flax should implement the View interface. With that step completed it should then create a new Renderer instance, passing the View in as a constructor parameter. It should also create a new Responder instance, passing in an Observable stream of actions as a constructor parameter. It should also handle disposing of disposables when the system destroys it.

## The View

Again, this is likely implemented by the Activity but is created as an interface. The interface should act as a proxy between the Renderer and the actual methods to invoke on Android’s view objects.

## The Responder

The Responder is responsible for responding to actions that propagate through the Observable. It should respond to those actions and ultimately pass the data to the Model which is managed by the Store.

## The Store

This is a very simple singleton which allows you lazily instantiate a Model and make updates to it. Since it is so simple I suppose I should go into some details of what the Model is/does. The Model stores the state of your View, it should be a one to one relationship. Whenever changes are made to the Model it should notify anything Observing on it that changes were made; in Flax there is a convenience method to invoke, _notifyModelChanged_.

## The Renderer

The Renderer observes the Model and when it receives notification that the Model changed it should invoke methods on the View to render the model to the user. This then closes the loop and returns us back to the View.

## Awesome, When Can I Use This?

Technically you can [start playing around with Flax now](https://github.com/CodyEngel/Flax). However I’m still in the process of fleshing everything out. There are still concepts that I need to expand on further such as Actions and Payloads. I still need build out a test harness and create some meaningful sample apps, preferably ones that actually handle networking. I’ll also need to figure out how I want to handle [managing Disposables](https://medium.com/@CodyEngel/managing-disposables-in-rxjava-2-for-android-388722ae1e8a).

So there is still a lot of work left for me to do, but for now I want to get community feedback and see what everyone thinks. Do you see any major (or minor) flaws with this?