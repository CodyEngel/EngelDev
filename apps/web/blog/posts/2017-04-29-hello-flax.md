---
title: Hello Flax — A Reactive Architecture For Android
date: 2017-04-29
description: Build reactive apps in Android with Flax.
tags:
  - flax
  - android
  - architecture
  - unidirectional data flow
  - mvi
  - software engineering
  - java
---

One of the current trends in Android is building apps expecting them to
be reactive and embracing that characteristic. The first time I heard
about this idea was back in October of 2016 when one of the engineers at
my company gave a talk about
[Flux](https://facebook.github.io/flux/). The main concepts
behind flux is you have a Dispatcher, Store, Action, and View. Through
flux you are able to transform a user interaction into data which can
then be transformed into information that is displayed to the user in a
view. It's sort of neat.

Then came the series of articles about
[Model-View-Intent by Hannes Dorfman](http://hannesdorfmann.com/android/mosby3-mvi-1). The premise behind this was sort-of similar to
Model-View-Presenter except you allow the view to render itself with an
immutable model. I then decided to give MVI a shot earlier this
month which is ultimately why I started working on Flax. I'll cover
that more later though.

A few weeks ago Devoxx released a talk from Jake Wharton where he
discusses
[managing state with RxJava](https://www.youtube.com/watch?v=0IKHxjkgop4). It's a really great talk and I strongly encourage everyone
to watch it, if you've already watched it, watch it again. My take away
from this talk is it's futile to build an app with the notion that it
can modeled synchronously. You need to build your apps in such a way
that you expect the data to change at any moment. So how can we achieve
this?

![Thanks Vittorio Zamboni and Unsplash for this awesome picture. It doesn't have much to do with this article, although flaxseed is something birds eat, right?](../../assets/images/source/2017-04-29-hello-flax.jpg)

## Hello Flax

As I said earlier Flax was born after I tried out MVI with the
stipulation that I remove the pesky presenter. In order to do this I
based the architecture off of Flux, this is also where I got the
incredibly unique and clever name. The idea is you should respond things
happening to the UI and adding state to a model. You should observe
changes on the model which can then be rendered on the view. It's a
fairly simple idea and creating the Hello Flax app was almost just as
easy (spoiler, the non-hello world sample was a bit harder and exposed a
lot of flaws with my original thinking).

So let's first look at what our View exposes to other classes to get a
better understanding of what this app will be doing.

```java
interface MainView extends FlaxView {

    void setText(CharSequence text);

}
```

Okay, so aside from my method naming being non-optimal (strange because
I'm usually stickler for that in code reviews) this still highlights a
few things, so let's talk about them.

1.  This view will provide the ability to set text on
    the screen. I don't really know where that text will be, but that's
    not really my concern.
2.  This view doesn't really let us do much with it.
    That's probably a good thing, after all this is a very simple app to
    demonstrate how to use Flax.

With that we'll take a look at what will be interacting with `MainView`,
within Flax that is known as a Renderer. The Renderer is responsible for
observing changes on a Model and extracting data from it to pass along
to the View.

```java
class MainRenderer extends FlaxRenderer<MainModel, MainView> {

    MainRenderer(MainView view) {
        super(view);
    }

    @Override
    protected void modelUpdated(MainModel updatedModel) {
        getView().setText(String.valueOf(updatedModel.getValue()));
    }

}
```

Okay, this is arguably more simplistic than `MainView` is, all we are
doing is overriding the abstract `modelUpdated` method, but still let's
dissect this.

1.  The `modelUpdated` method will be called whenever an
    update has been made to our Model.
2.  Our Renderer can then extract information from the
    Model and pass that information along to our view.
3.  Our Renderer can be smart if it needs to be (such as
    doing string formatting) or it can be fairly dumb and display the
    information as the Model gives it to us. In this case it knows that
    `getValue` will need to be converted to a String, so I suppose it's
    somewhere in the middle of what you should do with the
    Renderer.

So if you are coming from Model-View-Presenter this is essentially half
of your Presenter, it's the half that is concerned with calling methods
on your View. The other half is the Responder which I will get to in a
minute, but first I want to talk about the Model.

```java
public class MainModel extends FlaxModel<MainModel> {

    private Integer value = 0;

    public MainModel() {
        super();
    }

    void plus() {
        value++;
        notifyModelChanged();
    }

    Integer getValue() {
        return value;
    }

}
```

So you can see this Model stores an Integer and exposes two methods to
other classes within the same class. We've already seen what `getValue`
does from the Renderer above but the `plus` method is new to us, and
it's *bigly* important. When you call this method it will increment our
value and then it calls `notifyModelChanged`, if this sounds similar to
`notifyDatasetChanged` that's because I sort of stole the naming
convention from the RecyclerView, as they say ¯\\(ツ)\_/¯

Okay let's get back to what `notifyModelChanged` actually does though.
On the backend it will emit an `onNext` event to anything subscribed to
our Model. One thing that's useful to understand is `FlaxRenderer`
automatically subscribes to the Model for us, inheritance isn't always a
bad thing. So my general rule of thumb is any method that changes some
internal value of the Model should call `notifyModelChanged` after it's
done updating the values. Also, it *probably isn't* a good idea to call
`plus` from MainRenderer (this is why Flax is `0.1`).

Alright enough about the Model though, let's talk about where the other
half of the Presenter went. It's time to talk about the Responder.

```java
class MainResponder extends FlaxResponder<MainModel> {

    MainResponder(Observable<FlaxAction> actions) {
        super(actions);
    }

    @Override
    protected void actionReceived(FlaxAction flaxAction) {
        switch (flaxAction.getActionType()) {
            case FlaxAction.CLICK:
                if (flaxAction.getViewId() == R.id.button) {
                    getModel().plus();
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format(Locale.US, "FlaxAction Type %s Not Supported", flaxAction.getActionType()));
        }
    }

    @Override
    protected void errorReceived(Throwable error) {
        Log.e(getClass().getName(), error.getMessage());
    }

    @Override
    protected void completed() {
        Log.i(getClass().getName(), "Completed");
    }

}
```

Okay so the Responder is quite a bit larger than the other classes we've
seen so far, so I guess it must be more complex, right? Well, not
really, this has a few more abstract methods to implement which for this
app don't really do much. The important method is `actionReceived` which
will receive a `FlaxAction` and determine what to do with it; so let's
dissect that in isolation.

1.  Whenever we receive any `FlaxAction` the
    `actionReceived` method will be called.
2.  It's important for the Responder to determine what
    action was actually invoked, in this situation a switch statement is
    probably your bestie — do people even say that anymore?
3.  Once we've determined which action was received we
    should also figure out if a view caused that action, in this case we
    are checking if a button was clicked.
4.  Once we've isolated the action down to a type and a
    view we can confidently respond to this action and notify the model.
    In this case we call the `plus` method.

That wasn't so bad and now you can hopefully see that this replaces the
parts of your Presenter that would respond to user input or certain
lifecycle events (such as attach or detach). So with that let's go to
something more familiar, the MainActivity.

```java
public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.button) Button button;
    @BindView(R.id.text) TextView text;

    private FlaxResponder flaxResponder;
    private FlaxRenderer flaxRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        flaxRenderer = new MainRenderer(this);
        flaxResponder = new MainResponder(new FlaxActionObservableBuilder().mapClick(button).build());
    }

    @Override
    protected void onDestroy() {
        flaxRenderer.dispose();
        flaxResponder.dispose();
        super.onDestroy();
    }

    @Override
    public void setText(CharSequence text) {
        this.text.setText(text);
    }
}
```

In future versions of Flax I will most likely ship with a FlaxActivity
which will handle a lot of the inner workings. In my more complex sample
app I actually [created a base class](https://github.com/CodyEngel/Flax/blob/master/simplenetworking/src/main/java/com/codyengel/simplenetworking/AbstractFlaxActivity.java) which simplifies this, but for now let's discuss what's going
on.

1.  We are using
    [ButterKnife](http://jakewharton.github.io/butterknife/) to
    handle our view bindings. If you aren't familiar with that library
    (you should be), it essentially replaces `findViewById` with
    annotations.
2.  We have a reference of [FlaxResponder](https://github.com/CodyEngel/Flax/blob/master/flax/src/main/java/com/codyengel/flax/FlaxResponder.java) and [FlaxRenderer](https://github.com/CodyEngel/Flax/blob/master/flax/src/main/java/com/codyengel/flax/FlaxRenderer.java). These allow us to call their
    `dispose` methods to avoid leaking their internal
    Disposables.
3.  We are implementing our `MainView` which saves us
    from creating a composite view. You'll also probably be familiar
    with this if you are currently in the MVP world.
4.  We create the Renderer and Responder from the
    `onCreate` method, which can cause some issues in more complex apps
    so moving it to `onResume` wouldn't be bad either, just make sure
    you only instantiate instances when there isn't already an existing
    one.
5.  The Renderer requires you pass in the View while the
    Responder requires you pass in an Observable with the actions. I'm
    still trying to determine the best way to actually handle this. For
    now the `FlaxActionObservableBuilder` gets the job done.
6.  We are implementing the View's `setText`
    method.

And really that's it. With this app you click a FloatingActionButton and
the counter will increment by one. Since the Model isn't attached to the
Activity lifecycle you can rotate your phone or emulator and the data
will be restored to the last state the model was in. There is a decent
amount going on behind the scenes, but I won't go into the details in
this article, for now enjoy thinking all of this is magic.

## Get Building

At the time of this writing Flax is currently in the very early days.
While the 0.1 version number is mostly arbitrary it's also important to
understand that none of this is finalized. While I feel the concept is
fairly close to final I still need to add more test cases, ensure we
aren't leaking memory, come up with better naming conventions, and
actually figure out how publishing a library to jCenter works. For now
you can
[manually download Flax](https://github.com/CodyEngel/Flax/releases)
from the releases page. Oh, and of course you can view this project in
its entirety (along with a terrible diagram) on
[GitHub](https://github.com/CodyEngel/Flax). If you find any
issues while testing please
[file an issue](https://github.com/CodyEngel/Flax/issues), it can be
as small as issues with the syntax or as big as a memory leak.

I'm still getting into writing on Medium. My goal is to write at least
one article per week, if you enjoy my writing style consider clicking
the heart and following me.
