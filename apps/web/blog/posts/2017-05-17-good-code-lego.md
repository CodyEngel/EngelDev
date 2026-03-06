---
title: Good Code Is Like LEGO
date: 2017-05-17
description: Good code is like LEGO, it's built with a lot of smaller blocks to build a larger, end product.
tags:
  - software engineering
  - object oriented programming
---

# Good Code Is Like LEGO

![Thanks for the awesome picture Leonardo Melo over on Flickr!](../../assets/images/source/2017-05-17-good-code-lego.jpg)

When I was a kid I loved playing with LEGO. I think at one point I had
3,000 general purpose blocks, the trainset, and a few wild west sets.
The nice thing about LEGO is how modular the different pieces are. If
you are going to build something small then you likely don't need a
base, but if you are going to build something more complex you'll want a
base to help guide where the different pieces should go. Good code is a
lot like LEGO.

## Small Blocks Become Bigger Blocks

Pretend you need to build a house out of LEGO blocks. For this house we
are going to measure it out in terms of the knobs found on a LEGO base.
For this house you want it to be 97 knobs wide by 103 knobs deep. For
the side that's 97 knobs long you could use 24 of the 4 knob wide blocks
along with 1 of the 1 knob wide block. With that you were able to easily
satisfy that requirement. Now imagine the single knob block didn't
exist. Suddenly an easy task just became next to impossible without a
tool to cut a bigger block into a smaller one.

How about another example? You want to build a car, and for this car you
want it to have 2 wheels up front and 4 wheels in the back. With LEGO
you can grab each wheel and freely place it where you need to. Alright,
now imagine that the person before you decided that wheels should only
ever be arranged 2 up front and then after 12 knobs you'd have 2 more
(one on the left and one on the right). Suddenly this task became
impossible again.

These examples are meant to be examples of things all of us have been
guilty of at one time or another (and probably still are at times). It's
very easy to solve a problem in a very specialized way, and oftentimes
it is a little faster up front. In my years as a professional software
engineer I can say this with confidence: I have never kicked myself in
the butt because I made a class too small and general.

## Build Small General Classes

This is the part of the article where I give some advice, and that
advice is to build small general classes. As an example, let's say you
have to add a weather widget to your company dashboard. This widget will
include the current weather conditions for Chicago, IL and should be
updated every 15 minutes. You can build this out to work specifically
for this one use case, or you can break it up into smaller pieces and to
work for many other use cases. Let's go with the second option.

**Weather** will be a class which stores the data retrieved from some
API. It will allow you read information from it such as the current
temperature, cloud coverage, precipitation percentage, and wind speed.
In order to create this object you'll need to provide it the details I
just mentioned.

**WeatherRequest** will be used to request the current weather
conditions for a location from some API. It will not handle threading
nor will it handle parsing the response for you. It will simply take a
location and return a response from a server synchronously.

**WeatherParser** will be used to take a JSON response and return a
Weather object to you. This can be used in conjunction with
WeatherRequest. This is a little fragile since changes made to the
WeatherRequest source could potentially break how this works, but this
is just an example.

**WeatherView** will be used for displaying the current weather
conditions to the user.

**WeatherUpdater** will take a WeatherRequest, a WeatherParser, as well
as how often it should update. This will handle the threading and it
will notify any object that cares when it has an update, and this will
return a Weather object.

**WeatherController** will essentially orchestrate everything together.
It will take care of instantiating all of the above objects (except for
Weather) and it will act as a bridge between WeatherUpdater and
WeatherView.

Now imagine if requirements change and you now have to display current
weather conditions for Chicago, San Francisco, and Paris. Well this is
easy, simply update WeatherController to take location as a parameter
and instantiate three separate instances of it.

Now there is a new requirement to only automatically update the weather
conditions for Chicago. You can create a new class called
**WeatherDownloader** which will use WeatherRequest and WeatherParser
the same way that WeatherUpdater does, except it will only download the
weather once. See, because this was broken down it enabled us to respond
to changes fairly easy. Sure this could have been done in one or two
classes but it would have been harder to respond to these changes.

## What This Actually Means

My goal of this article is to highlight the usage of smaller reusable
classes. If you get in the habit of doing this then it would make the
next leap into using composition that much easier. With composition you
will essentially create an interface which will define different methods
but it won't provide a method body. From there you can create individual
classes for each one of those methods known as delegates, these
delegates can handle reuse of logic for the interface methods. Then
you'd finally have a class which can implement multiple interfaces and
it can decide which delegates to use for each method. And voila, you
have inheritance without the downsides.

Good code is like LEGO. It's built with a lot of smaller blocks to build
a larger, end product. It should be easy to take apart and rearrange
should requirements change.

That's it. This article is a little different from what I have been
posting.
