import fs from "fs";

export const data = {
    permalink: "/css/main.css",
    eleventyExcludeFromCollections: true,
};

export function render() {
    return [
        "./css/reset.css",
        "./css/tokens.css",
        "./css/global.css",
        "./css/layout.css",
        "./css/components.css",
        "./css/utilities.css",
    ].map(f => fs.readFileSync(f, "utf8")).join("\n");
}