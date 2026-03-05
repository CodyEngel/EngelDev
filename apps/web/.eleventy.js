import syntaxHighlight from "@11ty/eleventy-plugin-syntaxhighlight";
import embedYouTube from "eleventy-plugin-youtube-embed";
import { eleventyImageTransformPlugin } from '@11ty/eleventy-img';


export default function(eleventyConfig) {
    eleventyConfig.ignores.add("README.md");

    eleventyConfig.setLiquidOptions({
        jsTruthy: true
    });

    // Enable 11ty Plugins
    eleventyConfig.addPlugin(syntaxHighlight);
    eleventyConfig.addPlugin(embedYouTube);
    eleventyConfig.addPlugin(eleventyImageTransformPlugin, {
        extensions: 'html',
        formats: ['webp', 'gif', 'jpeg'],
        widths: [320, 570, 880, 1024, 2048],
        defaultAttributes: {
            loading: 'lazy',
            decoding: 'async',
            sizes: '90vw',
        },
        sharpOptions: {
            animated: true,
        },
        outputDir: './_site/assets/images/generated/',
        urlPath: '/assets/images/generated/',
    });

    // Passthrough copy
    eleventyConfig.addPassthroughCopy("css");
    eleventyConfig.addPassthroughCopy("js");
    eleventyConfig.addPassthroughCopy("assets");

    // Collections
    eleventyConfig.addCollection("posts", function(collectionApi) {
        return collectionApi.getFilteredByTag("post").sort((a, b) => b.date - a.date);
    });

    eleventyConfig.addCollection("projects", function(collectionApi) {
        return collectionApi.getFilteredByTag("project").sort((a, b) => b.date - a.date);
    });

    eleventyConfig.addCollection("featuredProjects", function(collectionApi) {
        return collectionApi.getFilteredByTag("project")
            .filter(item => item.data.featured)
            .sort((a, b) => b.date - a.date);
    });

    eleventyConfig.addCollection("tagList", function(collectionApi) {
        const tagSet = new Set();
        collectionApi.getAll().forEach(item => {
            (item.data.tags || []).forEach(tag => {
                if (!["post", "project", "page"].includes(tag)) {
                    tagSet.add(tag);
                }
            });
        });
        return [...tagSet].sort();
    });

    // Filters
    eleventyConfig.addFilter("readableDate", (dateObj) => {
        return new Date(dateObj).toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
            timeZone: "UTC"
        });
    });

    eleventyConfig.addFilter("isoDate", (dateObj) => {
        const d = new Date(dateObj);
        const year = d.getUTCFullYear();
        const month = String(d.getUTCMonth() + 1).padStart(2, "0");
        const day = String(d.getUTCDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    });

    eleventyConfig.addFilter("excerpt", (content, length) => {
        if (!content) return "";
        length = length || 160;
        const stripped = content.replace(/<[^>]*>/g, "");
        return stripped.length > length
            ? stripped.substring(0, length).trim() + "..."
            : stripped;
    });

    eleventyConfig.addFilter("limit", (arr, count) => {
        return (arr || []).slice(0, count);
    });

    // Shortcodes
    eleventyConfig.addShortcode("year", () => {
        return new Date().getFullYear().toString();
    });

    eleventyConfig.addShortcode("image", (src, alt, caption) => {
        let html = `<figure><img src="${src}" alt="${alt}" loading="lazy" decoding="async">`;
        if (caption) {
            html += `<figcaption>${caption}</figcaption>`;
        }
        html += `</figure>`;
        return html;
    });
};
