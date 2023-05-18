/* eslint-env node */
module.exports = {
    extends: ['eslint:recommended', 'plugin:@typescript-eslint/recommended'],
    parser: '@typescript-eslint/parser',
    plugins: ['@typescript-eslint'],
    root: true,
    rules: {
        "@typescript-eslint/no-empty-function": "off",
        "@typescript-eslint/no-var-requires": "off",
        "brace-style": ["error", "stroustrup", { "allowSingleLine": true }],
        "comma-dangle": ["error", "always-multiline"],
        "comma-spacing": "error",
        // include more rules here as needed
    }
};