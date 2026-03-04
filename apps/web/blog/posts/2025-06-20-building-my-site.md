---
title: Building My Personal Site
date: 2025-06-20
description: A look at how I built this site with Eleventy and custom CSS.
tags:
  - eleventy
  - css
  - web development
---

I recently finished rebuilding my personal site from scratch. Here's a quick overview of the decisions I made and why.

## Tech Stack

- **Eleventy** for static site generation
- **Liquid** for templating
- **Custom CSS** with CSS custom properties for theming
- **No JavaScript frameworks** on the client side

## Design Approach

I wanted a clean, readable design with a warm neutral color palette. The site supports both light and dark modes using CSS custom properties and a toggle button.

## CSS Architecture

Instead of reaching for a CSS framework, I organized my styles into logical files:

- `reset.css` for a clean baseline
- `tokens.css` for design tokens (colors, spacing, typography)
- `global.css` for base element styles
- `layout.css` for layout primitives
- `components.css` for UI components
- `utilities.css` for helper classes

This keeps the CSS maintainable without needing a build tool.
