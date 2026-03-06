---
name: medium-migrator
description: Migrate Medium blog posts to properly formatted markdown. Use when converting Medium article exports to clean markdown files with proper syntax, frontmatter, and image paths.
allowed-tools: Read, Glob, Edit, Write
---

# Medium Blog Post Migrator

Convert Medium blog post exports to clean, properly formatted markdown files.

## When to Use

- User asks to "migrate" a Medium article
- User opens a markdown file with residual HTML from Medium exports
- File contains `<figure>`, `<figcaption>`, `<a href>`, or `<img>` HTML tags
- File contains Medium-specific content like "Get X's stories in your inbox" or "Subscribe" buttons

## Migration Process

### 1. Read the File

First, read the entire markdown file to identify all HTML elements that need conversion.

### 2. Convert HTML to Markdown

Apply these conversions:

#### Links
```
BEFORE: <a href="https://example.com" class="..." rel="..." target="_blank">link text</a>
AFTER:  [link text](https://example.com)
```

#### Images with Figure/Figcaption
```
BEFORE:
<figure class="...">
<img src="_media/123456_image1.jpg" loading="eager" role="presentation" />
<figcaption>Caption text here</figcaption>
</figure>

AFTER:
![Caption text here](../../assets/images/source/YYYY-MM-DD-slug.jpg "Caption text here")
```

#### Images without Figcaption
Create descriptive alt text based on surrounding context.

#### Standalone Images
```
BEFORE: <img src="_media/123456_image1.jpg" loading="eager" role="presentation" />
AFTER:  ![Descriptive alt text](../../assets/images/source/YYYY-MM-DD-slug.jpg "Descriptive alt text")
```

### 3. Image Path Convention

Convert image paths from Medium's `_media/` format to the project's convention:
- Pattern: `../../assets/images/source/YYYY-MM-DD-slug.jpg`
- Date comes from the frontmatter `date` field
- Slug comes from the filename (without date prefix and extension)
- For multiple images, append `-1`, `-2`, etc.

Example for file `2017-06-26-why-snap-map-is-a-big-deal.md`:
- First image: `../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal-1.jpg`
- Second image: `../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal-2.jpg`

For files with a single hero image, omit the number suffix:
- `../../assets/images/source/2017-06-26-why-snap-map-is-a-big-deal.jpg`

### 4. Remove Medium-Specific Content

Delete these Medium artifacts:
- "Get [Author]'s stories in your inbox" sections
- "Join Medium for free" text
- "Subscribe" buttons (standalone text saying just "Subscribe")
- "Top highlight" labels
- "Press enter or click to view image in full size" text
- Base64-encoded SVG images (Medium UI elements)
- Empty `<figure>` tags
- `<mark>` tags (keep inner text)
- Internal Medium links (`/@username/...`) - either remove or keep just the text

### 5. Preserve Content

Keep these elements unchanged:
- YAML frontmatter (between `---` markers)
- Markdown formatting (headers, lists, blockquotes, bold, italic)
- Code blocks (but change `bash` to `java` for Java code)
- External links that are already in markdown format

### 6. Clean Up

- Remove duplicate H1 headers if title is already in frontmatter
- Remove trailing whitespace and extra blank lines
- Ensure single blank line between sections
- Remove trailing newlines at end of file (keep just one)

## Output

Write the entire converted file at once using the Write tool. This is more reliable than multiple Edit operations for files with complex multi-line HTML blocks.

## Quality Checklist

After migration, verify:
- [ ] No `<a href>` tags remain
- [ ] No `<figure>` or `<figcaption>` tags remain
- [ ] No `<img>` tags remain
- [ ] No Medium subscription sections remain
- [ ] All images have proper paths and alt text
- [ ] Code blocks have correct language tags
- [ ] Frontmatter is preserved

## Example

### Before
```markdown
---
title: My Article
date: 2017-05-17
---

# My Article

<figure class="nh ni nj nk nl my nm nn paragraph-image">
<img src="_media/853939_image1.jpg" loading="eager" role="presentation" />
<figcaption>Photo by Someone on Unsplash</figcaption>
</figure>

Check out <a href="https://example.com" class="z nw" rel="noopener" target="_blank">this link</a>.

## Get Author's stories in your inbox

Subscribe

Subscribe
```

### After
```markdown
---
title: My Article
date: 2017-05-17
---

![Photo by Someone on Unsplash](../../assets/images/source/2017-05-17-my-article.jpg "Photo by Someone on Unsplash")

Check out [this link](https://example.com).
```