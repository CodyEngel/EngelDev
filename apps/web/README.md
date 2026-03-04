# Static Site

This is the static site for engel.dev which is built with [11ty](https://www.11ty.dev/).

## Development

This project uses `pnpm` so be sure that has been installed and setup first.

```shell
pnpm --version      # verify installation
npm install -g pnpm # install if missing
```

### Run Eleventy

```shell
pnpm exec eleventy          # generates files which show up in the _site directory
pnpm exec eleventy --serve  # runs eleventy locally with a hot-reloading web server
```

## Troubleshooting

### Markdown files are being generated to HTML that shouldn't be

Be sure that those files are added to the `ignores` collection in the `.eleventyignore` file. Likewise if
a markdown file isn't being generated then make sure it's not in that ignorescollection.

