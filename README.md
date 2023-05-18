# tfip-miniproject

Miniproject for TFIP

## Using UI libraries together with Angular Material

- Each library has its own design language, so mixing them might result in an inconsistent look and feel
- Each library will add to the size of the final JS bundle which can impact performance
- However, there may be some libraries that pair well with Angular Material

## CSS Flexbox, CSS Grid

- asdf

## OpenAI

### Advantages of Using OpenAI's Libraries

- **Ease of use**
  - The libraries handle all the nitty-gritty details of setting up HTTP requests and parsing responses, enabling you to focus on the core functionality of your application.
- **Error handling**
  - The libraries are designed to handle common HTTP and API errors gracefully, reducing the amount of error-handling code you need to write.
- **Up-to-date features**
  - As OpenAI updates their API, the corresponding libraries are usually updated to support new features, often before documentation for the raw HTTP interface is available.
- **Community support**
  - The libraries often have an active community of users, meaning you can find solutions to common problems and get help if you're stuck.

### Possible architecture

- **Frontend**: Angular. This would handle user interactions.
- **Backend**: Spring Boot. This would handle business logic and database operations.
- **AI Service**: A separate Python or Node.js service. This would use OpenAI's libraries to interact with the OpenAI API.
- **Database**: MySQL, along with other databases as required.
- When a user interaction requires AI processing, the Spring Boot backend would send a request to the AI service, which would handle interacting with the OpenAI API and return the result to the backend, which would then return the result to the frontend.
- This architecture would allow you to utilize OpenAI's libraries while still maintaining a Spring Boot backend. It does add complexity to your system, so you would need to consider whether the benefits of using the libraries outweigh this added complexity.

### Python or Node.js?

- asdf

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
