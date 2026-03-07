---
title: "Seven Principles of Great Unit Tests — Adapted For Android"
date: 2018-07-23
description: Seven principles for writing great unit tests on Android. fast, independent, thorough, repeatable, professional, readable, and automatic.
tags:
  - android
  - kotlin
  - testing
  - unit tests
---

When it comes to software engineering it's usually easy to find sources about how to write good code that is ready for production. You can look into [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) or [SOLID Principles](https://hackernoon.com/solid-principles-made-easy-67b1246bcdf) (or both) and have a good idea of how to reliably write good code. However when it comes to writing good unit tests I've found we tend to throw good engineering practices out the window. What's even worse is testing on Android still isn't commonplace, this has resulted in lackluster information about how to test on the platform. So with all of that out of the way, what are the seven principles of great unit tests?

![Photo by chuttersnap on Unsplash](../../assets/images/source/2018-07-23-seven-principles-of-great-unit-tests-adapted-for-android-1.jpg "Photo by chuttersnap on Unsplash")

## 1. Fast

One of the major benefits of writing unit tests is it allows you to document how your code should work (as well as prove it actually works). The only way you can take advantage of that benefit is if your tests are fast. Engineers are impatient (as most people are), if it takes more than a second to verify your code works there is little chance you will want to run them with any sort of frequency. Running tests frequently is crucial to finding regressions in your code, the longer you wait, the more time you'll spend figuring out why a test is failing.

Making sure your unit tests are fast isn't something I cared about until I had to wait 15 minutes for 2,000 unit tests to execute. While writing tests for Android I've discovered a few things to avoid if you want to have fast tests. The first thing you should do is [say no to Robolectric](https://github.com/robolectric/robolectric/). While Robolectric has it's benefits, the last time I used it in a project I spent 30 seconds waiting for it to initialize in most tests. If you want to use Robolectric you should ensure it runs separately from your *actual* unit test suite.

The second thing you should do is limit your reliance on things like [PowerMock](https://github.com/powermock/powermock) since preparing a static function can take upwards of one second. In most cases you can avoid using PowerMock by writing code that doesn't rely heavily on statics.

The third is only use [Mockito](http://site.mockito.org/) when you actually need to, using it to mock a `data` class is probably overkill. However if you have some complex business logic you need to mock (such as a UseCase) then it's probably worth using a mocking framework.

Just for reference, the last CircleCI build we had for our Android app at [ActiveCampaign](https://www.activecampaign.com/) reported that our 246 unit tests took a total of 1.501 seconds to run. While we don't have that many unit tests currently (hey, we're only 10 weeks into this project), when we do reach 2,000 unit tests it should only take about 12 seconds to run the entire suite. We have been able to achieve these numbers by avoiding Robolectric and PowerMock as well as only using Mockito when we absolutely need to.

## 2. Independent

Yet another mistake I've seen are tests that are not indepdent of one another. When I say independent I mean two things: your tests only test one thing and your tests do not rely on one another to pass. So how can you avoid falling into these mistakes?

For testing one thing you should be able to write a sentence that explains what your test is doing and what you expect it to return. If you find yourself adding **and** to the sentence's expected behavior portion you're probably test is probably too large. Another way to know is if you are calling the function your testing more than once and asserting on the different outputs. Here's an example of a good unit test:

```kotlin
@Test
fun `string with http will remove http when calling extractSubDomain`() {
    val given = "http://activecampaign"
    assertThat(given.extractSubDomain()).isEqualTo("activecampaign")
}
```

This unit test is good because it's only testing one aspect of the function which is whether or not it will remove `http://` from a string. If something changes in our `extractSubDomain` function that prevents this from working correctly this test will fail however other tests around the same function should still pass. By making these tests independent of each other we are able to better document everything that we expect to happen while also knowing what exactly broke. So what does a bad unit test look like?

```kotlin
@Test
fun extractSubDomain_WorksCorrectly() {
    var given = "http://activecampaign"
    assertThat(given.extractSubDomain()).isEqualTo("activecampaign")
    given = "activecampaign.com"
    assertThat(given.extractSubDomain()).isEqualTo("activecampaign")
}
```

In this example we are updating our `given` value and trying to assert on two different inputs. This is bad for a number of reasons, but in terms of independent our second assertion is now relying on the first assertion to succeed. This means that if the code to remove the TLD works correctly but the code to remove the protocol does not we'll only know that the latter is broken. This may not seem like a big deal, but when you have a fix failing unit tests it's better to know up front exactly what is broken as opposed to only knowing about the first thing that fails.

The last thing to point out when it comes to independent tests is the way that test runners typically work. In general if you use [JUnit](https://junit.org/junit5/) you will probably have a hard time writing tests that rely on one another since they inherently will run randomly. Actually, it's more likely that you'll accidentally write tests that rely on one another which will surface as flakey tests out in the wild. Regardless though, in this case the testing framework is doing all of the work for you, so unless you have singletons that are seeding test data you should be safe.

## 3. Thorough

It doesn't matter how good your unit tests are if you hardly write them. If you are writing independent tests then being thorough is a fairly easy next step. While writing your unit tests you should at a minimum ensure all of the possible outputs are covered for a given input. In other-words for every condition in your function you should have at least one unit test that asserts it works correctly. In our example for independent tests we had a function called `extractSubDomain`, so let's expand on that further by showing what the actual function looks like.

```kotlin
fun String.extractSubDomain(): String {
    return replaceBefore("/", "")
        .replace("/", "")
        .replaceAfter(".", "")
        .replace(".", "")
}
```

This probably isn't what you'd expect to see as the solution may seem like it's made for RegEx (in the future maybe it will be). The body of this function is just four lines and yet we have a total of eight tests to ensure that this works correctly. Here are the names of those tests:

```kotlin
@Test
fun `string without any domain characteristics is left unchanged when calling extractSubDomain`() {}

@Test
fun `string with http will remove http when calling extractSubDomain`() {}

@Test
fun `string with https will remove https when calling extractSubDomain`() {}

@Test
fun `string with tld will remove tld when calling extractSubDomain`() {}

@Test
fun `string with domain name will remove domain name when calling extractSubDomain`() {}

@Test
fun `string with tld and http will remove everything except the subdomain when calling extractSubDomain`() {
    //note: and is okay in this case as it's describing the input
}

@Test
fun `string with domain name and http will remove everything except subdomain when calling extractSubDomain`() {}

@Test
fun `string with domain name and https will remove everything except subdomain when calling extractSubDomain`() {}
```

This many tests is necessary because the subdomain could be `http://medium.activecampaign.com`, `http://medium`, `medium.activecampaign.com`, etc. So simply testing that we remove one piece or another isn't enough as we also need to ensure thing a complete domain protocol, domain, and TLD can still be removed so the sub domain can be extracted. This of course is not perfect, you may notice that `activecampaign.com` will result in `activecampaign` being returned; for the problem we are solving though it is actually adequate. When the solution is no longer enough we can make `extractSubDomain()` more robust while ensuring the past functionality still works.

On the other-hand, if we weren't thorough with these unit tests we could have inadvertently added blind spots to our code. Those blind spots can result in regressions cropping up without the test suite failing. So when it comes to writing good unit tests, make sure you're thorough. Anytime a regression slips through into production it's usually a sign that your test suite was not as thorough as it could have been (although this is fine so long as you write the test to prove the bug exists and then fix the code).

![Photo by Lucas Vasques on Unsplash](../../assets/images/source/2018-07-23-seven-principles-of-great-unit-tests-adapted-for-android-2.jpg "Photo by Lucas Vasques on Unsplash")

## 4. Repeatable

The fastest way to get your team to stop writing tests is by writing flakey ones. A flakey test is similar to a flakey friend, sometimes they show up and other times they break CircleCI. These are the kinds of tests that will pass one minute but then fail for no apparent reason the next. A good unit test should produce the same result each and every time.

The only way you can ensure your unit tests are repeatable is by removing every piece of uncertainty in your system. In the Android world this means your unit tests should never touch your I/O stack, whether it be your local SQLite database or a remote API. If you're using Room and Retrofit you should take advantage of the fact that both of those rely heavily on interfaces which you can easily create a mock or fake instances of for your tests.

Another thing to keep in mind is that anything which will save state between test functions or test classes will eventually cause your tests to start flaking. A typical example of this would be trying to test a class that relies on a singleton, while it's fine to use that pattern you should ensure your test classes have a way of instantiating their own instance of the object.

## 5. Professional

Most of us spend about eight hours per day building Android apps that are easy to maintain. Typically applications that are easy to maintain have loose coupling between classes and are DRY (don't repeat yourself). As it turns out, unit tests aren't special, they should be designed in the same way your production code. In fact, your unit test requirements may influence design decisions of your production code as was the case with our StringLoader interface.

```kotlin
interface StringLoader {
    fun getString(stringId: Int): String

    fun getString(stringId: Int, vararg formatArgs: Any): String
}
```

The production implementation simply routes these calls to `Context.getString(...)`, however the test implementation makes it possible for us to supply our own values that map to a given value which makes it possible to assertions that wouldn't be possible previously. Our `StringLoaderFake` gets a bit more interesting though.

```kotlin
class StringLoaderFake : StringLoader {
    data class StringResValue(
        val resourceId: Int,
        val value: String = Randomizer.string()
    )

    private val stringMap = HashMap<Int, String>()

    fun expect(stringResValue: StringResValue) {
        stringMap[stringResValue.resourceId] = stringResValue.value
    }

    override fun getString(stringId: Int)
        = stringMap[stringId] ?: stringNotFound()

    override fun getString(
        stringId: Int,
        vararg formatArgs: Any
    ): String {
        return stringMap[stringId]?.format(*formatArgs)
            ?: stringNotFound()
    }

    private fun stringNotFound(): String {
        throw IllegalStateException(
            "getString was called before value was set"
        )
    }
}
```

Anywhere that we need to write unit tests that are expecting a certain String ID be invoked just uses the `StringLoaderFake` implementation. You'll notice that we supply a default value for `StringResValue.value` which further cuts down on the boilerplate code you need to write. Here's an example of what this looks like in an unit test:

```kotlin
@Test
fun `when download contact list info returns no contacts error the correct message state should be present`() {
    expectDownloadContactInfoResponse(
        Response(
            error = Response.Error.NoContacts
        )
    )
    val title = StringResValue(R.string.no_contacts_title)
    val message = StringResValue(R.string.no_contacts_text)
    stringLoader.expect(title)
    stringLoader.expect(message)

    initViewModel()

    assertMessageState(
        MessageState.Error(
            title = title.value,
            message = message.value
        )
    )
}
```

As you can see the `StringResValue` class not only lets us provide an expected response to our `StringLoader` implementation, it also let's us keep track of what the actual return value will be. Then providing that the correct path was taken in our class under test we should see the same string is returned elsewhere in the code. This is going beyond just writing code under a `@Test` annotation, this is building a framework to make your tests easier to write and more reliable.

One last thing to touch on when it comes to writing professional unit tests. Oftentimes you may notice you are asserting similar things from one test to another. An easy win is to extract your assertion logic into a private function, going back to our example above, here is what our `assertMessageState` looks like:

```kotlin
private fun assertViewState(
    state: (currentState: ContactsViewState) -> Unit
) {
    assertThat(contactsViewModel.viewState.value).isNotNull
    state.invoke(contactsViewModel.viewState.value!!)
}

private fun assertMessageState(expectedMessageState: MessageState) {
    assertViewState { currentState ->
        assertThat(currentState.messageState)
            .isEqualTo(expectedMessageState)
    }
}
```

Without these helper functions we would need to retrieve the view state everytime, check that it isn't null, and then check the message state is the same as the one we expect. It may not seem like much, but it adds up.

## 6. Readable

Unit tests that are difficult to read are ones that will eventually be deleted when an engineer writes code that makes them fail. When writing your unit tests it's important to be very clear with what they are testing while also cutting down on the amount of code required to write the them.

Android officially started to support Kotlin in 2017. One of my favorite features that I learned about recently is the ability to use backticks around function names which lets you write sentences for your function names. This was already shown as part of previous code examples but it's worth giving another example as it will likely change how you describe your unit tests.

```kotlin
fun `string with https will remove https when calling extractSubDomain`() { ... }
```

That function describes what our test is all about perfectly. Not only that but the function body and it's associated helper function are incredibly easy to comprehend.

```kotlin
@Test
fun `string with https will remove https when calling extractSubDomain`() {
    validateSubDomain(
        expect = randomDomain,
        beforeModifier = "https://"
    )
}

private fun validateSubDomain(
    expect: String,
    beforeModifier: String = "",
    afterModifier: String = ""
) {
    val given = "$beforeModifier$expect$afterModifier"
    assertExtractSubDomain(given, expect)
}

private fun assertExtractSubDomain(given: String, expect: String) {
    assertThat(given.extractSubDomain()).isEqualTo(expect)
}
```

The test itself invokes `validateSubDomain` which then handles the general setup of the test case, also known as the **given** portion. The `validateSubDomain` function in turn invokes `assertExtractSubDomain` which will handle the **when**, **then** portion of our test case. Readability starts with the function name but it should carry through into the function body as well.

## 7. Automatic

It doesn't matter if you have 110% coverage on your unit tests with 100,000 test cases that can execute in under one minute if you never run them. Good unit tests need to be invoked automatically and they should be ran regularly. What this translates into is making use of a continuous integration product, at [ActiveCampaign we use CircleCI](https://www.activecampaign.com/blog/inside-activecampaign/ember-parallel-testing-code-coverage-reports-at-activecampaign/), but there are many other options available as well. Our setup blocks merging code into `develop` or `master` until all of our quality checks pass, one of those quality checks is no failing unit tests. This process is tied into GitHub as well, so it's not just the honor system, if our CI pipeline finds anything wrong it will stop you from merging until it is fixed.

This may seem like a lot of work, but once you get in the habit of following these seven principles you'll find it fits into your development process fairly easily. If this was useful, feel free to share it with friends or colleagues.