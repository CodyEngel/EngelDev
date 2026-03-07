---
title: "How I Learned Nothing Is Impossible, Musings From A Software Engineer"
date: 2017-09-11
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# How I Learned Nothing Is Impossible, Musings From A Software Engineer

I first started programming in 2007 and quickly failed my high school
programming class. That resulted in not taking the follow-up course the
next semester and transferring into sewing. I like to think that was a
blessing, while I never became proficient in Visual Basic I can easily
patch up favorite shirts or hem things.

It was then in 2010 after I had been modifying WordPress themes for my
various blogs that I decided I should learn about PHP. So I purchased a
book on PHP & MySQL and soon realized I wasn’t terrible at it.

Since then I’ve been met with various tasks that sounded impossible at
the time but in hindsight were just learning opportunities. In this
article I’d like to bring up three things which seemed impossible at the
time, how I overcame it, and what I learned from it.

<figure class="ph pi pj pk pl pg bd paragraph-image">
<img src="_media/375430_image1.jpg" loading="eager"
role="presentation" />
<figcaption>It only seemed fitting to use a Cubs World Series photo for
any article talking about anything being possible. This photo is
licensed for non-commercial use from <a
href="https://www.flickr.com/photos/shutterrunner/31078068746/in/photolist-Pmge6u-7Rk5D1-WjB2fr-67fsAa-WwguH4-WwgF4z-3973KD-bA5th9-5BGPrC-ooXDLp-7RaDX6-X8zJ-396LH8-396HYt-396KP8-NTQUcv-NHwN1L-Nrgg8Y-NHwHg5-NHwMcm-NTQWk8-NrgfN9-NrghiU-NTQXfz-GMfMaz-eRGV3z-d4CBXS-7EJ1Z2-6t2Mu6-396LqK-ehoX2u-6bws2u-5HQmXt-4NVWV8-2SNJe-B9y1a-34vGme-B9xkt-2nkQK-4qHyo-5qY9qo-34AgMj-VgpKhy-5o1Jh3-VjoHy6-4UHkyC-bE4Zg4-cPVdHb-bX3fb8-7EJeAx"
class="z pt" rel="noopener ugc nofollow" target="_blank">Shutter
Runner</a>.</figcaption>
</figure>

## Building A Client Without A Backend

By far one of my favorite projects I’ve worked on to date was when I
worked for a digital agency. We had a father-son startup approach us
with an idea for building an inventory management solution for
contractors. As with most software projects we were working on a tight
three month deadline to build a client on Android as well as a backend
to support it. It was going to take roughly three months to build out
the backend along with another three months to complete the mobile app,
so we built them in parallel.

I started out by determining what data models I would need to create to
support the designs. From there I created a network layer which didn’t
actually return anything from the network and instead returned hardcoded
models. This allowed the majority of the mobile app to be built out
while the backend engineer focused on building out a database that would
support the complex requirements.

Good communication was a key to the success of this project. When it was
time to design the API I was able to let the backend engineer know what
the ideal request and response would be. While some compromises had to
be made, once the API endpoints were ready to implement they were up and
running in the app within a day. This was back when I wrote the JSON
parsing myself, using something like GSON it would have been ready
before lunch.

**When the timeline seems impossible start looking at how you can break
the project up into smaller pieces that can be done in parallel.**

## Achieving 80% Code Coverage When You Never Wrote A Unit Test

Have you ever had a senior manager that demanded 80% code coverage with
unit tests? If you haven’t, don’t worry, one day you will. This happened
to me while I was working at a Fortune 100 company. At the time I had
never written a unit test and even worse Android didn’t have many
examples to go off of.

I first started out by looking at how Java developers would write unit
tests. There was a great course I found on Lynda which allowed me to get
an idea of what a unit test was and what it should be targeting. From
there I started out with simple things like writing tests for my data
models. Sure I was just testing that a `String` I just set is still
going to return that same value, but it was a low hanging fruit and
forced me to design a model that was easy to test.

Once I felt comfortable writing simple tests I decided to test a custom
view in Android. This required mocking parts of the Android framework
(which is no small task) and so I found a library for that called
Robolectric. From here I could verify that if I call `setText` or
`setBackground` that the view was responding correctly. While this was
more complex it didn’t involve testing anything network related so it
was a decent next step.

<a
href="http://robolectric.org/?source=post_page-----7402916e09de---------------------------------------"
rel="noopener  ugc nofollow" target="_blank"></a>

## Robolectric

### Robolectric is a unit test framework that de-fangs the Android SDK jar so you can test-drive the development of your…

robolectric.org

The final step was figuring out how to test our networking layer. This
led to creating a wrapper around our networking library which allowed me
to swap it out when running unit tests. This wasn’t 100% ideal, but
given my current skill-set it was the best I could come up with, and it
worked. Just before our first release we had 80% code coverage in our
app with the majority of untested code being framework specific things
like displaying and interacting with a map.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

**When you have no idea how to do something and you have to suddenly
become an “expert”, start out small and work your way up. Understand
that when learning something done is better than perfect as you learn
more from a completed project than something that’s half-complete.**

## Being An Engineer When You Hate Math

As my current bio on Medium says, I hate math. When I was a kid I wasn’t
terrible at math, actually it was the subject I performed best in
(language arts was my weakest subject). However once I got to high
school and college I just started to really dislike the courses. This is
around the time I started learning Algebra. The homework from this point
on required multiple sheets of paper and working on very tedious
problems that I frankly wasn’t too interested in. This resulted in me
taking the less demanding math courses in college. I kind of regret this
as I found that I really enjoyed discrete math, business algebra, and
statistics.

None the less, I’m one of those engineers that doesn’t have a strong
math background. I’ve been able to be successful by realizing there are
other areas that are equally important to writing good software and
focusing on those areas instead. These areas include: data management,
object-oriented programming, software documentation, and test driven
development.

<a
href="https://blog.codinghorror.com/should-competent-programmers-be-mathematically-inclined/?source=post_page-----7402916e09de---------------------------------------"
rel="noopener  ugc nofollow" target="_blank"></a>

## Should Competent Programmers be "Mathematically Inclined"?

### One of the more famous Edsger Dijkstra quotes is from his 1972 Turing award lecture, How do we tell truths that might…

blog.codinghorror.com

Relational databases have always made sense to me. Breaking up a system
into smaller pieces of data and then drawing a relationship between them
also extends to object-oriented programming. Understanding how to design
a complex system with smaller objects means you can have a cohesive
application which is easy to update and maintain. It also means that if
you suck at math and write something that doesn’t perform well, someone
else can swoop in and make it run better without having to change
anything else. Providing there are well-written tests around those
objects it also means you can easily refactor the code which isn’t
performing as well as it could without having to worry about
regressions.

So I focused on the other areas of software engineering that don’t
require an in-depth understanding of trigonometry or algebraic topology.
There are engineers out there that enjoy learning about that, I enjoy
focusing on designing systems that are easy to maintain.

**It’s important to understand your strengths and weaknesses as well as
the strengths of others on your team. Spend time working on areas that
you can conceivably get better at and don’t stress out when you need to
lean on someone else on the areas you struggle with.**

## Okay, Some Things Are Impossible

I am worried that by saying everything is possible I’m ignoring that
certain circumstances can make things impossible. If you are expecting
to build Facebook in one week; that’s impossible. If you plan on
releasing a project next week which has already taken months and is only
50% complete, that’s probably impossible.

Time is a finite resource. I’ve been on projects which were never going
to release on time, the team had let management know, and the solution
was to double the team size one month before the expected release. Not
only did the project not release on time, it was delayed further because
myself and other engineers on the project had to split our time
on-boarding the new engineers. Outside of that we also over-engineered
certain things because we didn’t have a clear plan for how the product
would be used. Focusing more on the core features and less on the
what-ifs would have saved weeks on the project.

Project estimation and clear communication between engineering and
product are important for the success of a project. Without either of
those, something which was possible could become impossible. However
given enough time, anything is possible.

**Most of the time when something presents itself as impossible it’s
usually an indication that you don’t have enough time to complete the
project. Start early, make sure you have adequate staffing, and
prioritize functionality.**

At Yello, the word no is not in our vocabulary. Whether the problem is
simple or complex, we like to take on new challenges. Success might not
be easy, but the Yello team always finds a way to make the impossible…
possible. The next time you get discouraged by a problem just remember
that we were able to land on the moon with less processing power than
the phone currently in your pocket.

<a
href="https://yello.co/?source=post_page-----7402916e09de---------------------------------------"
rel="noopener  ugc nofollow" target="_blank"></a>

## Yello Talent Acquisition Software For Recruiters \| Yello

### Yello offers talent acquisition software to source, recruit, and hire great candidates. Say Yello and find out how we…

yello.co

Thanks for taking the time to read my article. If you enjoyed it feel
free to clap or applaud this, or if you have something you’d like to add
leave a response below. I’m also on Twitter, I usually try to post links
to my articles on there as well as do mini-Medium posts in 140
characters or less, feel free to follow me on there.

<a
href="https://twitter.com/POTUS404?source=post_page-----7402916e09de---------------------------------------"
rel="noopener  ugc nofollow" target="_blank"></a>

## Cody Engel (@POTUS404) \| Twitter

### The latest Tweets from Cody Engel (@POTUS404): "Dear apartment owners: please post pictures of the apartment and now…

twitter.com

