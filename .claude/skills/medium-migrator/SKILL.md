---
name: medium-migrator
description: Migrate Medium blog posts to properly formatted markdown. Use when converting Medium article exports to clean markdown files with proper syntax, frontmatter, and image paths. Also handles finding and processing all pending migration files when asked to "migrate articles" or "run migrations".
allowed-tools: Read, Glob, Edit, Write
---

# Medium Blog Post Migrator

Convert Medium blog post exports to clean, properly formatted markdown files.

## When to Use

- User asks to "migrate articles", "run migrations", or similar
- User asks to migrate a specific file
- A file in `apps/web/blog/posts/` contains `migrationPending: true` in its frontmatter
- File contains `<figure>`, `<figcaption>`, `<a href>`, or `<img>` HTML tags
- File contains Medium-specific content like "Get X's stories in your inbox" or "Subscribe" buttons

## Migration Process

### 1. Find Pending Files

Glob `apps/web/blog/posts/*.md` and filter for files containing `migrationPending: true` in their frontmatter. Process each file top-to-bottom in filename order (which is chronological given the `YYYY-MM-DD-` prefix).

### 2. For Each Pending File

#### 2a. Read the File

Read the entire file to understand its full content before making any changes.

#### 2b. Convert HTML to Markdown

Apply these conversions:

##### Links
```
BEFORE: <a href="https://example.com" class="..." rel="..." target="_blank">link text</a>
AFTER:  [link text](https://example.com)
```

##### Images with Figure/Figcaption
```
BEFORE:
<figure class="...">
<img src="_media/123456_image1.jpg" loading="eager" role="presentation" />
<figcaption>Caption text here</figcaption>
</figure>

AFTER:
![Caption text here](../../assets/images/source/YYYY-MM-DD-slug-1.jpg "Caption text here")
```

##### Images without Figcaption
Create descriptive alt text based on surrounding context.

##### Standalone Images
```
BEFORE: <img src="_media/123456_image1.jpg" loading="eager" role="presentation" />
AFTER:  ![Descriptive alt text](../../assets/images/source/YYYY-MM-DD-slug-1.jpg "Descriptive alt text")
```

#### 2c. Image Path Convention

Convert image paths from Medium's `_media/` format to the project's convention:
- Pattern: `../../assets/images/source/YYYY-MM-DD-slug-{N}.ext`
- Date and slug come from the post's filename (e.g. `2017-06-26-why-snap-map-is-a-big-deal.md`)
- The number (`{N}`) is preserved from the original Medium filename (e.g. `406767_image11.jpg` → `-11`)
- Extension is preserved as-is (`.jpg` stays `.jpg`, `.gif` stays `.gif`)

Example for `2017-06-26-why-snap-map-is-a-big-deal.md`:
- `_media/406767_image1.jpg` → `../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal-1.jpg`
- `_media/406767_image11.gif` → `../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal-11.gif`

#### 2d. Remove Medium-Specific Content

Delete these Medium artifacts:
- "Get [Author]'s stories in your inbox" sections
- "Join Medium for free" text
- "Subscribe" buttons (standalone text saying just "Subscribe")
- "Top highlight" labels
- "Press enter or click to view image in full size" text
- Base64-encoded SVG images (Medium UI elements)
- Empty `<figure>` tags
- `<mark>` tags (keep inner text)
- Internal Medium links (`/@username/...`) — remove or keep just the text

#### 2e. Preserve Content

Keep these elements unchanged:
- YAML frontmatter (between `---` markers) — aside from the fields being filled in below
- Markdown formatting (headers, lists, blockquotes, bold, italic)
- Code blocks (but change `bash` to `java` for Java code)
- External links that are already in markdown format

#### 2f. Clean Up Body

- Remove the H1 title (it is already in frontmatter)
- Remove trailing whitespace and extra blank lines
- Ensure single blank line between sections
- Remove trailing newlines at end of file (keep just one)

#### 2g. Rewrite the Outro

Find the closing paragraph(s) that reference Medium-specific calls to action such as recommending the article, clicking the heart icon, or following on Twitter/social media. Replace with:

> If you enjoyed this article, feel free to share it with friends, family, or colleagues.

Adapt the wording naturally to fit the article's tone — but keep it brief (one sentence) and never mention liking, commenting, following, or any platform-specific actions.

#### 2h. Generate Description

Write a concise description of the article for the `description` frontmatter field. Requirements:
- Must be 140 characters or fewer
- Should be a plain-language summary of what the article is about
- Do not begin with "In this article" or similar filler phrases
- Replace the `SHORT DESCRIPTION HERE` placeholder

#### 2i. Generate Tags

Replace the placeholder tags in the frontmatter with 2–5 relevant tags. Guidelines:
- For general/opinion articles: use broad topic tags (e.g. `social-media`, `product`, `design`)
- For technical articles: include the key technologies discussed (e.g. `android`, `kotlin`, `firebase`)
- Use lowercase kebab-case
- More technical articles may have more tags — up to ~8 is fine if warranted
- Remove the `REPLACE ME` placeholder entries entirely

#### 2j. Remove Migration Flag

Remove the `migrationPending: true` line from the frontmatter.

### 3. Write the File

Write the entire converted file at once using the Write tool. This is more reliable than multiple Edit operations for files with complex multi-line HTML blocks.

## Quality Checklist

After writing each file, verify:
- [ ] No `<a href>` tags remain
- [ ] No `<figure>` or `<figcaption>` tags remain
- [ ] No `<img>` tags remain
- [ ] No Medium subscription sections remain
- [ ] All images have proper paths, preserved numbers, and preserved extensions
- [ ] Code blocks have correct language tags
- [ ] Frontmatter is complete: `title`, `date`, `description` (≤140 chars), `tags` (real values)
- [ ] `migrationPending: true` has been removed
- [ ] Outro has been rewritten

## Example

### Before
```markdown
---
title: "Why Snap Map is a Big Deal"
date: 2017-06-26
description: SHORT DESCRIPTION HERE
tags:
  - REPLACE ME
  - REPLACE ME TOO
migrationPending: true
---

# Why Snap Map is a Big Deal

<figure class="nh ni nj nk nl my nm nn paragraph-image">
<img src="_media/853939_image3.jpg" loading="eager" role="presentation" />
<figcaption>Photo by Someone on Unsplash</figcaption>
</figure>

Check out <a href="https://example.com" class="z nw" rel="noopener" target="_blank">this link</a>.

If you enjoyed this article be sure to recommend it by clicking the heart icon and following me on Twitter.
```

### After
```markdown
---
title: "Why Snap Map is a Big Deal"
date: 2017-06-26
description: A look at why Snapchat's Snap Map feature is a more significant product moment than it first appears.
tags:
  - snapchat
  - product
  - social-media
---

![Photo by Someone on Unsplash](../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal-3.jpg "Photo by Someone on Unsplash")

Check out [this link](https://example.com).

If you enjoyed this article, feel free to share it with friends, family, or colleagues.
```