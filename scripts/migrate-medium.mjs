#!/usr/bin/env node
import { execSync } from 'child_process';
import {
  readFileSync,
  writeFileSync,
  mkdirSync,
  renameSync,
  unlinkSync,
  rmSync,
  existsSync,
  readdirSync,
} from 'fs';
import { join } from 'path';
import { fileURLToPath } from 'url';

const __dirname = fileURLToPath(new URL('.', import.meta.url));
const ROOT = join(__dirname, '..');
const ARTICLES_FILE = join(ROOT, 'articles-to-migrate.md');
const POSTS_DIR = join(ROOT, 'apps/web/blog/posts');
const IMAGES_DIR = join(ROOT, 'apps/web/images/source');
const MEDIA_DIR = join(ROOT, '_media');
const BLOG_MD = join(ROOT, 'blog.md');

// ---------------------------------------------------------------------------
// Parsing
// ---------------------------------------------------------------------------

function parseArticles(content) {
  const articles = [];
  // Split on list items, keeping the delimiter
  const blocks = content.split(/(?=^- url:)/m).filter(s => s.trim());

  for (const block of blocks) {
    const url   = block.match(/url:\s*(.+)/)?.[1]?.trim();
    const date  = block.match(/date:\s*(.+)/)?.[1]?.trim();
    const title = block.match(/title:\s*(.+)/)?.[1]?.trim();
    const done  = /^\s*done:\s*true/m.test(block);
    if (url && date && title) {
      articles.push({ url, date, title, done, raw: block });
    }
  }

  return articles;
}

function serializeArticles(articles) {
  return articles.map(a => {
    let block = `- url: ${a.url}\n  date: ${a.date}\n  title: ${a.title}\n`;
    if (a.done) block += `  done: true\n`;
    return block;
  }).join('\n');
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function titleToSlug(title) {
  return title
    .toLowerCase()
    .replace(/['']/g, '')        // remove smart quotes / apostrophes
    .replace(/[^a-z0-9\s-]/g, '') // strip non-alphanumeric
    .trim()
    .replace(/\s+/g, '-');
}

function log(msg) {
  console.log(msg);
}

// ---------------------------------------------------------------------------
// Per-article work
// ---------------------------------------------------------------------------

function processImages(fileBase) {
  if (!existsSync(MEDIA_DIR)) {
    log('  ⚠ No _media directory found — skipping image processing.');
    return;
  }

  mkdirSync(IMAGES_DIR, { recursive: true });

  const images = readdirSync(MEDIA_DIR);

  for (const img of images) {
    // Expected pattern: 406767_image0.jpg, 406767_image11.gif, etc.
    const match = img.match(/^.+_image(\d+)(\.[a-z0-9]+)$/i);
    if (!match) {
      log(`  ⚠ Unexpected filename in _media, skipping: ${img}`);
      continue;
    }

    const num = parseInt(match[1], 10);
    const ext = match[2].toLowerCase();

    if (num === 0) {
      unlinkSync(join(MEDIA_DIR, img));
      log(`  Deleted profile picture: ${img}`);
      continue;
    }

    const newName = `${fileBase}-${num}${ext}`;
    renameSync(join(MEDIA_DIR, img), join(IMAGES_DIR, newName));
    log(`  Moved: ${img} → ${newName}`);
  }

  rmSync(MEDIA_DIR, { recursive: true, force: true });
}

function createPost(postPath, article, blogContent) {
  const frontmatter = [
    '---',
    `title: "${article.title}"`,
    `date: ${article.date}`,
    `description: SHORT DESCRIPTION HERE`,
    `tags: `,
    '  - REPLACE ME',
    '  - REPLACE ME TOO',
    'migrationPending: true',
    '---',
    '',
  ].join('\n');

  writeFileSync(postPath, frontmatter + blogContent);
}

function migrateArticle(article, articles) {
  const slug     = titleToSlug(article.title);
  const fileBase = `${article.date}-${slug}`;
  const postPath = join(POSTS_DIR, `${fileBase}.md`);

  log(`\n── ${article.title}`);
  log(`   slug: ${fileBase}`);

  if (existsSync(postPath)) {
    log(`  ⚠ Post already exists at ${postPath} — skipping.`);
    return;
  }

  // 1. Download
  log('  Downloading via mediummd...');
  try {
    execSync(`mediummd ${article.url}`, { cwd: ROOT, stdio: 'inherit' });
  } catch (err) {
    log(`  ✗ mediummd failed: ${err.message}`);
    return;
  }

  if (!existsSync(BLOG_MD)) {
    log('  ✗ blog.md not found after download — skipping.');
    return;
  }

  // 2. Images
  processImages(fileBase);

  // 3. Create post
  mkdirSync(POSTS_DIR, { recursive: true });
  const blogContent = readFileSync(BLOG_MD, 'utf8');
  createPost(postPath, article, blogContent);
  log(`  Created: apps/web/blog/posts/${fileBase}.md`);

  // 4. Cleanup
  unlinkSync(BLOG_MD);

  // 5. Mark done and persist immediately (so a crash mid-batch doesn't lose progress)
  article.done = true;
  writeFileSync(ARTICLES_FILE, serializeArticles(articles));
  log(`  ✓ Marked done`);
}

// ---------------------------------------------------------------------------
// Main
// ---------------------------------------------------------------------------

function main() {
  if (!existsSync(ARTICLES_FILE)) {
    console.error(`articles-to-migrate.md not found at ${ARTICLES_FILE}`);
    process.exit(1);
  }

  const content  = readFileSync(ARTICLES_FILE, 'utf8');
  const articles = parseArticles(content);
  const pending  = articles.filter(a => !a.done);

  log(`Found ${articles.length} articles total, ${pending.length} pending.\n`);

  if (pending.length === 0) {
    log('Nothing to do.');
    return;
  }

  for (const article of pending) {
    migrateArticle(article, articles);
  }

  log('\nAll done.');
}

main();
