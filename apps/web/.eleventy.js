module.exports = function(eleventyConfig) {
    eleventyConfig.ignores.add("README.md");

    eleventyConfig.setLiquidOptions({
        jsTruthy: true
    })
}