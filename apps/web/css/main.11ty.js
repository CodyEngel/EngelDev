import fs from "fs";

export const data = {
    permalink: "/css/main.css",
    eleventyExcludeFromCollections: true,
};

export function render() {
    return [
        "./css/fonts.css",
        "./css/reset.css",
        "./css/tokens.css",
        "./css/global.css",
        "./css/layout.css",
        "./css/components.css",
        "./css/code.css",
        "./css/motion.css",
        "./css/utilities.css",
    ].map(f => fs.readFileSync(f, "utf8")).join("\n");
}
