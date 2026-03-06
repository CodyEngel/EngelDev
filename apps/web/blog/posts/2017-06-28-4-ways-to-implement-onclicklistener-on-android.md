---
title: 4 Ways To Implement OnClickListener On Android
date: 2017-06-28
description: Ever wonder how many ways you can implement OnClickListener on Android?
tags:
  - android
  - software engineering
---

Ever since I created my first Android app in 2011 (it was a name
generator) I have relied heavily on implementing `OnClickListener` to
make my apps work. More recently I have been exploring different ways to
implement interfaces within classes and wanted to extend what I have
learned in the form of different ways to implement this very common
interface.

If you aren't an Android developer then all you need to know is you can
receive callbacks when a button or other view is tapped through
`OnClickListener` which has a method named `onClick`. With that in mind
you can replace `OnClickListener` with any other interface or protocol
and this should still be relevant to you.

Here are four ways to implement `OnClickListener` in Android without the
use of a third party library.

![Thanks for the eye catching image, Jeremy Bishop.](../../assets/images/source/2017-06-28-4-ways-to-implement-onclicklistener-on-android.jpg "Thanks for the eye catching image, Jeremy Bishop.")

## Option One

The first option we're going to look at involves defining our
`OnClickListener` within the method call site which can be found
starting at line 10. This is the way I learned to handle click events on
views back in 2011. For several years I was fairly content with this
method, but it is now something I almost never do.

```java
public class AwesomeButtonActivity extends AppCompatActivity {

    private Button awesomeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeButton = new Button(this);

        awesomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awesomeButtonClicked();
            }
        });
    }

    private void awesomeButtonClicked() {
        awesomeButton.setText("AWESOME!");
    }
}
```

One reason why I avoid this is because it clutters up your `onCreate`
method. This becomes even more apparent when you want to observe click
events from multiple views. Of course
[retrolambda](https://github.com/orfjackal/retrolambda) or simply
using [Kotlin](https://kotlinlang.org/docs/reference/lambdas.html)
would make it possible to take this from 5 lines down to a single line.

Fear not, I have two more reasons to avoid this. The next reason to stop
doing this is because it doesn't promote code reuse. Let's say I want to
call `awesomeButtonClicked()` for three new buttons, our designer
decided it'd be three times as awesome so we need to do it. This route
forces us to copy & paste our implementation three times. In general if
your code reuse strategy involves copy and paste that's an indication
you are doing something wrong. Don't copypasta.

The final, and probably best reason to avoid this is this is really
difficult to unit test. You can of course use
[Robolectric](https://github.com/robolectric/robolectric) to test what
happens when you invoke the click method on `awesomeButton`, and that's
really about your only option. On the other hand if you provided more
seams or areas to inject functionality into this class you could change
the implementation of this click event to be a mock or fake and can test
this without needing a heavy framework.

If you tend to implement `OnClickListener` using this method you aren't
doing anything wrong. As already said I did things this way for many
years, just be aware of some of the drawbacks and limitations that come
with doing things this way.

## Option Two

The second option is kind of like the first one except we assign the
implementation to field in the class. This has been one of my goto ways
to implement `OnClickListener` as of recently.

```java
public class AwesomeButtonActivity extends AppCompatActivity {

    private Button awesomeButton;
    
    private View.OnClickListener awesomeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            awesomeButtonClicked();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeButton = new Button(this);

        awesomeButton.setOnClickListener(awesomeOnClickListener);
    }

    private void awesomeButtonClicked() {
        awesomeButton.setText("AWESOME!");
    }
}
```

One reason why I like this option is it is incredibly easy to refactor
option one into option two. All you have to do is take your
implementation and move it out of `setOnClickListener`, assign it to a
field, and then update your call to `setOnClickListener` to reference
that field.

Another reason why I like this option is it allows you to reuse the
implementation. So when one of our customers demands that we add a
second button that does the exact same thing as our
[OG](http://www.urbandictionary.com/define.php?term=OG) awesome button, all
we have to do is call `setOnClickListener` and pass in the field.

The last reason I wanted to bring up is this creates a seam that will
allow us to test this class easier. In the example above we can use [Powermock's
Whitebox Class](http://static.javadoc.io/org.powermock/powermock-reflect/1.6.4/org/powermock/reflect/Whitebox.html)
to replace the field with a mock or stub of
`OnClickListener`.

Oh, another positive to this route is it can help you organize your code
a bit better as well.

## Option Three

This option takes option two one step further by declaring a class to
implement `OnClickListener`. So we now have a class named
`AwesomeButtonClick` which allows us to pass in a new instance of the
class when we call `setOnClickListener`. If we need to use this in
multiple areas you can also define the instance as a field. I've been
using this option recently as well.

```java
public class AwesomeButtonActivity extends AppCompatActivity {

    private Button awesomeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeButton = new Button(this);

        awesomeButton.setOnClickListener(new AwesomeButtonClick());
    }

    private void awesomeButtonClicked() {
        awesomeButton.setText("AWESOME!");
    }
    
    class AwesomeButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            awesomeButtonClicked();
        }
    }
}
```

This option works out fairly well on `OnClickListener` needs to have
it's own fields. For example: we want to maintain a count of how many
times `onClick` was invoked and adding `int count = 0` to this class and
then incrementing that count would be simple and it'd feel natural as
well.

Another reason why I like this option is helps to organize your code.
You can easily collapse this class and forget about it until you need to
look at it, and you will probably define all of your other methods for
the Activity before this class; so you don't mix up your methods (more
on that in option four).

The other reason why this is nice is it is a very simple refactor to
turn this into it's own class that can be referenced in other areas of
the app.

## Option Four

This option involves your Activity implementing `OnClickListener`, while
it certainly gets the job done it has become something I'm not a huge
fan of. Before I get into the reasons why I'm not a fan let's first take
a look at the code where you can see we have added `onClick(View v)` as
a public method on our Activity and we are now passing in `this` when we
call `setOnClickListener`. Okay, now let me explain why I'm not a huge
fan of this.

```java
public class AwesomeButtonActivity extends AppCompatActivity implements View.OnClickListener {

    private Button awesomeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeButton = new Button(this);

        awesomeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        awesomeButtonClicked();
    }

    private void awesomeButtonClicked() {
        awesomeButton.setText("AWESOME!");
    }
    
}
```

The first reason is this really isn't a robust solution. If you add
another button you now have to determine which button was clicked,
typically that'll involve a `switch/case` statement. In the past I've
said it's not very performant but honestly wasting a cycle or two for
each button click isn't the end of the world.

Another reason this option isn't my favorite is it exposes
implementation details to the outside world. Now anything that has
access to our `AwesomeButtonActivity` is now aware that it handles
`onClick` events. It also means that I could technically do something
stupid like passing in a new instance of `AwesomeButtonActivity` into a
button within a different Activity to reuse the `onClick` method's
logic. I would hope I'd get fired before I could do something like that,
but the fact that it's possible and will likely compile is a good
indication that it might not be the best route.

The last and biggest reason why I try to avoid this is it becomes
difficult to organize your class. Imagine you have a fairly robust
Activity which implements five of six other interfaces. Suddenly all of
the methods from these interfaces are intertwined together, this becomes
more evident after you've added methods to some of those interfaces.

Oh, and a bonus reason to avoid this. What happens if you need to
implement another interface with a method named `onClick`?

Again that's not to say that you shouldn't do this for other interfaces
or even for `OnClickListener`, my recommendation if you are about to do
this is to ask yourself if you really need to expose these details to
the outside world.

## This Can Be Applied To Any Interface

So while this was targetting `OnClickListener` I want to point out that
you can extend this knowledge to decide how to implement other
interfaces. In general my favorites are option two and three however I
will use the other options when it makes sense.

I also want to point out I haven't ran any benchmarks on the differences
in these approaches. As far as I know the differences would be fairly
minimal and probably wouldn't be worth the time, however if you have
experienced any gotchas with the options highlighted above I'd love to
hear about them in the responses section below.

As an aside for the RxJava users in my audience, it's worth looking at
replacing your typical view callbacks with equivalents found in
[RxBinding](https://github.com/JakeWharton/RxBinding).

Thanks for taking the time to read my article. If you enjoyed it feel
free to share it with your grandma.