# tfip-miniproject

Miniproject for TFIP

## Setting up

### Spring Boot

- Spring Initialiser
- Extract zip file contents into separate folder

### Angular

- In root directory
  - `ng new <name>`

### Node.js with TypeScript

- Make new directory
  - `npm init -y`
  - TypeScript, eslint
    - `npm install --save-dev @typescript-eslint/parser @typescript-eslint/eslint-plugin eslint typescript`

#### Configurations

- `package.json`

```json
"scripts": {
    "build": "tsc",
    "test": "echo \"Error: no test specified\" && exit 1",
    "start": "node dist/index.js"
},
```

- `tsconfig.json`

```json
{
    "compilerOptions": {
        "module": "NodeNext",
        "moduleResolution": "NodeNext",
        "target": "ES2020",
        "sourceMap": true,
        "outDir": "dist",
    },
    "include": ["src/**/*"],
}
```

- `.eslintrc.cjs`

```javascript
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
```

- `.gitignore`

```text
# Node.js dependencies
node_modules

# TypeScript build output
dist

# dotenv environment variables file
.env

# Logs
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Runtime data
pids
*.pid
*.seed
*.log

# IDEs and text editors
.vscode
*.swp
*.swo
*.swn
.idea

# OS generated files
.DS_Store
Thumbs.db
```

#### Running the program

```bash
npm run build
node dist/index.js
```

- With start script

```bash
npm run build
npm start
```