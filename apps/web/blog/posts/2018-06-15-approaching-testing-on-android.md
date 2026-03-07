---
title: "Approaching Testing on Android"
date: 2018-06-15
description: SHORT DESCRIPTION HERE
tags: 
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---


# Approaching Testing on Android

I joined <a href="https://www.activecampaign.com" class="z ph"
rel="noopener ugc nofollow" target="_blank">ActiveCampaign</a> in
mid-April and since that time, myself and the rest of the Android team
have been marching towards delivering the company’s first Android app.
The ways I’m measuring success for this product as an engineer include:
are we happy with the product we’re delivering, and is it well tested?
I’d like to delve deeper into the *is it well tested* part as I feel
like this is still a fairly foreign idea on Android, even in 2018. This
is not to say that Android engineers don’t write tests, we just
generally lack good information about why and how to write effective
tests on the platform.

<figure class="pk pl pm pn po pj bd paragraph-image">
<img src="_media/415406_image1.jpg" loading="eager"
role="presentation" />
<figcaption>Photo by <a
href="https://unsplash.com/photos/3GZi6OpSDcY?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow" target="_blank">Nicolas
Thomas</a> on <a
href="https://unsplash.com/search/photos/testing?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText"
class="z ph" rel="noopener ugc nofollow"
target="_blank">Unsplash</a></figcaption>
</figure>

## Prove It Works

In college we are taught how to create data structures, algorithms,
compilers, and how to write programs in languages like Assembly and C.
This leaves us, the student, to figure out how to test our code. When I
was in college (and at the start of my professional career) I would test
my code in the same way the end-user would use the application. If I
added a login form that would validate an email address, I’d hammer away
at the application to see if any value I typed in would show up as
invalid. Not to throw shade on end-to-end testing, but testing in the
way I just described is awfully unproductive, really ineffective, and
just does not scale.

**Automating end to end tests is extremely difficult**, as there are a
lot of moving pieces. My general rule on complexity is based on how many
moving pieces there are, the more moving pieces you have, the more
complex and difficult it will be to test. When I try to log into an app
and I receive an error, do I know what caused that error immediately?
Probably not, it could be incorrect credentials, the API could be down,
or the code involved for the login screen could be broken. **Those are a
lot of factors to take into consideration**, and every time you see
something not working you’ll have no idea exactly why it isn’t working.

In reality what you want to do as an engineer is test in smaller units,
if the unit is small enough it’s known as a **unit test**. To truly take
advantage of testing you should write out your test cases before writing
the code to make the tests pass. These test cases should describe what
your code is going to do and then you should write the code to satisfy
those requirements. **This is incredibly difficult to start doing**,
because your first instinct will be to write the code, manually test it,
then write unit tests for it wondering why you even need unit tests. The
code you produce in this manner will likely be vastly different if your
only focus was was to go from a failing test case to a passing one.

For example, the app we’re working on we has a requirement to format a
URL so it only contains the customer’s account name. So if your account
is called Medium and the supplied String was
`https://medium.activecampaign.com` the expected result would be
`medium`. Likewise if the String was just `medium` it should remain the
same. Great, this sounds like a regex problem, so time to jump over to
<a href="https://regexr.com" class="z ph" rel="noopener ugc nofollow"
target="_blank">RegExr</a> and start building out a pattern to match
such a scenario, right? Actually the current solution is a bit more
crude and surprisingly easier to come up with:

```bash
fun String.extractSubDomain(): String {
    return replaceBefore("/", "")
        .replace("/", "")
        .replaceAfter(".", "")
        .replace(".", "")
}
```

How do I know that this solution works correctly? I know it works
becuase there are about 10 unit tests written to test the different
scenarios that we plan to encounter. Is it bullet-proof? Maybe, maybe
not. If we do find a bug with this code we can add another test case
which proves the bug stems from this function. From there, that test
will fail and we will write the code to make the test pass, at the same
time we’ll know that the other 10 cases did not break.

**So that is why we write tests.** We write tests to prove that our code
does what we think it does. We write tests because it gives us the
freedom to write code that satisfies the requirements instead of falling
into <a href="https://martinfowler.com/bliki/Yagni.html" class="z ph"
rel="noopener ugc nofollow" target="_blank">YAGNI</a> land. We write
tests to document how the code is expected to work. We write tests so we
can refactor with confidence so the naive looking `extractSubDomain`
code can transform into a more sophisticated regex solution the day we
find a scenario that breaks it.

## The Testing Trifecta

Okay cool, so by focusing on TDD (test driven development), specifically
with unit tests the engineers get a ton of benefits. However unit tests
alone won’t save the day as they only give you assurance that the code
works by itself, it doesn’t let you know that the code works within the
larger system.

<figure class="pk pl pm pn po pj ps pt paragraph-image">

<img src="_media/415406_image2.jpg" loading="lazy"
role="presentation" />

<figcaption>100% Unit Test Coverage, 0% Integration Tests, 0% End to End
Tests</figcaption>
</figure>

Real world examples always seem like the best way to highlight the
issue. In isolation, the drawers pictured above worked perfectly and it
was only when they were put together to create a larger system that they
no longer worked as expected. This example sometimes tries to poke holes
into unit testing, some will say it’s completely pointless to write unit
tests. However the real issue is that the code we write is oftentimes
used in more complex systems and those complex systems need to be tested
like the less complex units of code.

## Get Cody Engel’s stories in your inbox

Join Medium for free to get updates from this writer.

Subscribe

Subscribe

<img
src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMSIgaGVpZ2h0PSIxMSIgdmlld2JveD0iMCAwIDExIDExIiBjbGFzcz0ic3oiPjxwYXRoIGQ9Im0wIDYuMzEzIDMuNzA0IDMuNzA1LjkwNC45MDQuNjYtMS4wOTUgNS4yOTYtOC43OTVMOC44NSAwIDMuNTU0IDguNzk1bDEuNTYzLS4xOTEtMy43MDQtMy43MDV6IiAvPjwvc3ZnPg=="
class="sz" />

Remember me for faster sign in

That brings us to the testing trifecta, also known as the
<a href="https://martinfowler.com/bliki/TestPyramid.html" class="z ph"
rel="noopener ugc nofollow" target="_blank">testing pyramid</a>. As
general guidance your total number of tests should be 70% unit, 20%
integration, and 10% end to end. In other words, if you have 10,000
tests you should have 7,000 unit tests, 2,000 integration tests, and
1,000 end to end tests. This isn’t a requirement, it’s merely guidance
to say that you should have an over abundance of unit tests, far fewer
integration tests, and then even fewer end to end tests.

Going back to the previous example for formatting the customer’s
account, we will execute the `extractSubDomain` code after the account
field loses focus. Our **integration tests** may only test that if it’s
the account name within a URL, after losing focus it will only be the
account name along with another test to ensure that if you only enter
the account name it leaves it alone. These tests offers a greater degree
of certainty that your system works together, but should one fail it
will take a bit more time to investigate which component caused that
regression.

Further removed from that, our **end to end tests** may not even care if
the account is formatted properly. It will enter in different
information and just ensure that when you tap the login button that you
login successfully (or unsuccessfully if we are testing the error path).
These should be testing the entire end to end experience which will
often require making real network calls to a real backend service. These
tests are often very difficult and time consuming to get in place
because of the number of engineers it requires to set them up.

**The testing trifecta is necessary to guarantee quality is sustained
over the lifetime of an Android app**. Without setting out with a clear
plan to test your work in a way that can be automated you’ll eventually
hit a wall where you can’t release consistently because the time to
regression test will take longer than an entire sprint. This wall may be
hit after several months or it may take over a year, but at some point
manual regression testing will cease to work as it just isn’t scalable.

## Testing Should Start Day One

Testing an Android app requires a lot of setup work. That retrofit
interface you are using needs to have a test implementation so you can
avoid making actual network calls. All of those DAOs you have defined
with Room, they too require test implementations. Your data models will
require an easy way to be generated and you should ideally have a way to
test against randomized data unless you require specific values for a
test.

When you don’t start testing at the start of a product you will
encounter a long uphill battle. All of the code you wrote likely isn’t
easy to test because it will lack those test implementations. The code
will probably be coupled in weird ways, you’ll find that you’ve used
`private` to hide a lot of logic which will now need to be abstracted
into another class which is usually easier said than done.

## So how should we approach testing on Android?

We should embrace it from day one. It’s the only way we as engineers can
prove that the code we wrote actually does what it should do. Testing
frees us from coming up with overly complex solutions to simple
problems. Testing gives us an easy way to introduce new functionality
without causing regressions.

I understand this is perhaps a long winded way to say that Android
developers should be testing their code. My goal for this article is to
be a starting place to share how we test our Android products at
ActiveCampaign. I hope some of our techniques will spark ideas for
others. I’m also sure some of our testing practices won’t be 100%
perfect and I’m excited to get feedback from the larger developer
community on things that don’t make sense. It’s 2018, testing shouldn’t
feel so foreign on Android.

Of course, if you feel like I’m stating the obvious and want to make a
well-tested and great app,
<a href="https://www.activecampaign.com/about/careers/" class="z ph"
rel="noopener ugc nofollow" target="_blank">we’re hiring</a>!

