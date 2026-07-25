---
title: "AI Decision Fatigue: The Cost of Shipping 8x More Code"
date: 2026-07-24
description: "My team shipped 8x more code with AI agents. The tradeoff was 48 to 87 consequential decisions a day — and the exhaustion that comes with them."
tags:
  - ai
  - software engineering
  - mental health
  - productivity
---

Houston, we have a problem, and it's not just the AI data centers.

I recently wrapped up a project at work to build out an AI first codebase. Today I want to share the mental fatigue I've faced through the increase in decisions I'm making daily as I fear it will move from an acute condition to a chronic one. AI absorbed the execution and left me with nothing but the decisions. Those decisions don't compound, because everything I teach an agent vanishes when the session ends. That's a pretty bold claim, even bolder without background knowledge of my work, so give me a few paragraphs to explain (or skip ahead if you already kinda believe me).

## The Road to Decision Fatigue

The team I'm on consists of four incredibly talented and driven software engineers. While we're all software engineers, we have diverse backgrounds and skill-sets including test, web, iOS and Android engineering. In the past month the four of us merged nearly 400 pull requests, added 280,000 lines of code, and built out a mostly autonomous AI coding agent delivery machine. That's about 92 pull requests per person, in a month.

Lines of code and merged pull requests are shallow measures. Neither translates cleanly to business value. But the order of magnitude is real: going all in on AI, as opposed to being a frequent user of it, put our output somewhere around 8x what the four of us could do before. I don't have a perfectly clean baseline for that, and I'd be suspicious of anyone who claims one. Call it a rough 8x.

Here's the part that doesn't get talked about. The decisions scaled with it. Every one of those pull requests needed someone to decide whether it was right. Every issue needed someone to decide what "done" meant before an agent could start. Output went up 8x, and so did the number of judgment calls required to produce it — probably more, since we were also building a repeatable system for other teams to follow.

At face value this paints an optimistic future. Businesses have no shortage of ideas, so being able to ship 8x more in a month without increasing headcount leads to increased revenue and ideally increased job security. I'm genuinely excited about this aspect of using AI to deliver more at a faster clip. What I'm not excited about is what the path to that number actually looked like.

Most days follow a similar schedule.

**9am:** open my laptop, review 3-6 pull requests that were opened overnight. For the most part I'm not looking at the code that has been written but instead assessing the feedback that other coding agents left to decide what changes are needed. If this is an important component in the codebase then I do a more thorough review of the code myself.

On average each pull request requires around 3 decisions, so I'm at 9-18 before finishing my coffee.

**10am:** it's time to figure out what I will work on for the day. This involves looking through our issue board, deciding what's most important, and also understanding what others on the team are working on. Once I have an accurate enough picture I can start scoping the work. I make use of agents to write the first draft for all the tasks (they are able to write requirements and review the codebase faster than I can), review and revise the tasks, and then send them off for coding agents to implement. Our team-sync generally happens in the middle of this.

Each issue is probably 5-10 decisions, so assuming there are only 3 issues for the day that's another 15-30.

**1pm:** there are more pull requests for me to review, and it's pretty much the same song and dance as what happened at the start of my day. This is also when my day becomes a bit more chaotic as I may have to steer the AI agents more heavily, or I might have to jump into an area of the codebase that didn't exist two days ago to figure out how a component works so that I can provide useful guidance. During this time I am also testing out the app to validate it is providing a pleasant user experience (and creating follow-up issues when it's not).

Assuming it's only 3 pull requests that's another 9 decisions.

**3pm:** around this time I start getting more issues together that agents can work on overnight. These issues range from new features to infrastructure improvements for our agentic harness.

Sometimes this is 2 or 3 issues, sometimes it's 10+ but 3 has been a good number so let's say another 15-30 decisions.

**5pm:** it's time to sign-off for the day. Thankfully most days end on time. There have been a handful of days that ended up being late nights which I mostly attribute to being excited about what we're building out.

This is a generalization of my schedule. Some days are more focused on feature delivery while other days are more focused on agentic harness development to correct issues from past runs. The number of decisions I'm making on any given day is between 48-87. These aren't deciding if I should use recursion or a loop. These are decisions that could result in my peers being blocked on their work. They are architectural trade-offs for "good-enough" and having AI come back through to resolve issues that were allowed to make it into an internal deployment. They are taxing and mentally draining decisions.

In a 2018 interview, [Jeff Bezos said that if he makes three good decisions a day, that's enough](https://qz.com/work/1390844/jeff-bezos-only-expects-himself-to-make-three-good-decisions-a-day) — and that Warren Buffett is happy making three good decisions a year. I'm not going to pretend my decisions are at the same level as theirs, but I have to imagine forty-eight to eighty-seven net-new decisions in a day (the usual decisions I was making prior to this project still exist) isn't sustainable long-term.

## Decision Fatigue and AI

Talking about decision fatigue when it comes to AI is hard. Despite almost everyone using AI to some extent during the day, there is next to no empathy when I talk about decision fatigue. I think it's because the output is visible and the cost isn't. When you ship 8x more than you used to, saying you're exhausted sounds like complaining about a promotion.

The best way I've found to describe this is you're working with a team of interns. Those interns are brilliant, they can write code that works well, and they take direction flawlessly. These interns do not have any real world experience though and so if you don't explain things clearly they will probably mess things up. Of course, you can give them further direction and they will fix those mistakes. At the end of the task you and the AI intern have learned a lot. Once the code changes are merged in they vanish though. All of that time and energy you poured into teaching the intern is gone. Also, it's not just one intern, you were probably working with four or five interns who all did the same thing.

Imagine how exhausting that must be?

Most people I talk to have had that experience of training someone and going through the trials and tribulations just to have them quit and start over from the start with another new hire. Imagine if that was every single day of your life, and you went through that heart break multiple times. This is what drives that decision fatigue. You are constantly in that mentor loop which traditionally is exhausting and rewarding, except in an AI world it's just exhausting.

So far, decision fatigue is an acute condition that resolves itself once you can take a break. I am skeptical that decision fatigue stays that way in the future especially as I'm reading more articles about ["AI brain fry"](https://fortune.com/2026/03/10/ai-brain-fry-workplace-productivity-bcg-study/). In those articles many of the reported symptoms like brain fog, being overwhelmed, exhausted, and suboptimal decision-making sound familiar...

Recognizing those symptoms is half the battle though and thankfully (for myself at least) I think I have found a way to keep my sanity (still unsure if this will be the long term solution though).

## The Antidote

Mentoring is worth the exhaustion because it pays you back. The intern gets better, and your job gets easier. Agents never pay you back. So you have to build that payoff yourself.

That starts with reflecting on how much work you completed and being okay with getting less done. This isn't to say getting less done than you did without AI, instead it's about recognizing when you've hit enough for the day. The way that works for me is by keeping track of these in a list.

Avoid the instinct to have yet another to do list though. Instead of a to do list I started keeping a did do list. On this list I keep track of what I did that day shortly after I did the thing. This includes when I feed my dog breakfast, when I attended a meeting, and every single Linear Issue I completed or started for the day. When it's only noon and you've filled a scratch pad with things you completed it's much easier to sign-off from work at a normal time.

Another thing I've found that works pretty well is to sprinkle in some easy AI tasks while working through the harder ones. This week I was stuck on enabling push notifications. It was a complex task as there were many integration points where something could go wrong (and a lot of things did go wrong). However, I also gave AI tasks like "update our dog food report description to be 5 lines tall." Those little tasks kept me energized because it required very little thought on my part and the agents were able to complete the work without bothering me.

These have helped keep my sanity. They don't fix the problem though. Someone still needs to solve the part where the work got 8x bigger while the individual's capacity for decisions did not. Nonetheless, they are helpful coping mechanisms.