---
title: "Early And Often — How To Release Software"
date: 2018-12-09
description: Why releasing software every two weeks beats infrequent releases — smaller scope, drama-free deploys, and immediate customer feedback.
tags:
  - software-engineering
  - agile
  - android
  - product
---

Most companies I have worked for were agile in the sense that we used JIRA, stories were pointed, and we planned in two week increments. Most of the time the code we wrote was kept on a shelf for several months before it released to our customers which isn't incredibly ideal. At [ActiveCampaign](https://www.activecampaign.com/) I've had the joy of releasing software every two weeks. Whether the release contains simple UI refreshes or is a major feature one thing is consistent, every two weeks we ship.

![Photo by Dan Gold](../../assets/images/source/2018-12-09-early-and-often-how-to-release-software-1.jpg "Photo by Dan Gold")

## Our Releases Are Drama Free

The first thing worth discussing is how drama free our releases are. If you are releasing software once a year, twice a year, or four times a year then you're doing yourself a disservice. For starters your muscle memory will never build up. Being able to release with ease won't be part of your process because you do it so rarely. You'll make a mistake somewhere which will cause chaos either internally or externally depending on the extent of that mistake.

Every other Friday my team evaluates the current state of our work. In some cases we've completed everything we committed to, in which case we just do a final pass to make things are working and get the release ready. In other cases we have some work in a half-way done state, in those cases we ensure the work is staged behind a feature flag and do some final testing and get the release ready. In either case, we go into the day knowing that our primary goal is to have a release ready by the end of the day and I'm happy to say we have yet to miss a release.

Releasing every two weeks also forced us to do up-front work to automate as much of the process as possible. Our basic flow is to stage work on develop once it's in a good state ready to be shared with our team internally. Once we are ready to stage the release for external customers we merge develop into master, and that's basically it.

Since my team focuses on Android development all of our releases are managed through Google Play. We leverage the internal test channel to share builds from develop with our co-workers, the APKs (binaries) are installed through the same automatic update mechanism used for actual Play Store deployments. We then use Alpha, and Production to stage our builds cut from master. Builds from the Alpha channel can be smoke tested by developers, design, and product. Providing everything looks good on Alpha we click a button in the Play Console to promote the build from Alpha to Production and then it's released.

Drama free releases are amazing.

## The Scope Of Change Is Small

Small scope goes along with the drama free releases. Whenever I've shipped software with a large scope of change it has usually required hours (sometimes days) of regression testing. You almost always uncover a bug where feature A didn't think of feature B and now feature C is completely broken, because you know, software.

This honestly isn't the case when your scope of change is focused to a two week release cycle. One of co-workers came up with a recurring meeting aptly titled the *Bug Safari* where he discusses the changes going into the release and the developers hammer away at the app for 30 minutes. We typically uncover low severity bugs that we queue up to fix in the next release. Sometimes we find show-stoppers which we work on resolving prior to releasing. Again though, since the scope is small, the amount of things that can go wrong is fairly small too.

## Customer Feedback Is Immediate

My team is currently working on a CRM app for our small and mid-size businesses. The user we are targeting is the sales-person in the field actively closing sales. The app started by viewing a list of every customer in the account and you could view details about each customer and find out if there were any CRM related items attached to the customer.

Once we got it in the hands of users though we found that this view was fairly inconsequential as they almost never care about the specific customer and instead care about the sale they are hoping to close out. This dramatically cut the data loading requirements for our application while also helping us deliver the most valuable thing to our users earlier. Being able to talk to users about how they are currently using the product and what they want to do in the future is a huge benefit to releasing early and often.

Our apps also ship with a variety of metric collecting tools. As an engineer I care about how the application is performing in key areas, so we monitor all network calls for success rates as well as how long they took to complete. We also monitor how fast certain tasks take to execute to determine if we need to further optimize them (this helps us release working code as opposed to over-engineered code).

We monitor our apps for fatal (crashes) and non-fatal errors. This helps us determine if we missed something somewhere, most of the time these are fairly minor crashes, however almost every-time it occurs it's something we likely never would have caught in manual regression tests (so getting the app to a broader audience earlier is very helpful).

We collect various events from our users to keep track of how often they complete certain tasks. This helps us determine if our hypothesis for a feature was a correct or not. Using analytics also helps settle disagreements during grooming for features, if we keep going back and forth on how a user *might* use a feature we just go with the path of the least resistance and through the analytic event we determine if further refinement is needed.

## Hot Fixes Are Rare

The final point I think is worth mentioning is hot fixes are very infrequent for us in this model. If you release software once a quarter or less then almost any defect warrants a hot fix. When you release every two weeks then almost nothing warrants an off-cycle release. On our team some items that would require a hot fix include:

1. We see a spike in crashes around one area of the app. If it affects a large number of our user base then it must be fixed immediately.
2. We notice that incorrect data is being generated by our app.
3. A feature is just turned off completely.

From this list number 3 is the only one that has actually happened so far. We changed how our app handled permissions which caused us to disable the ability to add a contact through the app. This was not intentional and we had a release ready to go the following day. Something worth noting, since our release process is so automated the task of releasing the hot fix was incredibly easy and drama free.

How often does your team release software? I'd love to hear about how your team approaches releases. If this article was useful, feel free to share it with friends or colleagues.