# Node.js server with TypeScript

## Setup

- Setting up directory and dependencies
  - `mkdir <directory_name>`, `cd <directory_name>`
  - `npm init -y`
  - `npm install --save openai`
  - `npm install dotenv`: For `.env` files
  - `npm install --save-dev @types/node @typescript-eslint/eslint-plugin @typescript-eslint/parser eslint nodemon ts-node typescript`
- `src` folder
  - `mkdir src`
- Configuration files are below

### Nodemon

- Nodemon will watch for changes in `*.ts` files in the `/src` directory
- Then recompiles and restarts
- After configuration, run `npm run dev`

### Configurations

- `package.json`

```json
"scripts": {
    "build": "tsc",
    "dev": "nodemon"
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

### Development mode, compilation

- To compile

```bash
npm run build
```

- To run in development mode with `nodemon

```bash
npm run dev
```

### Linting

- `npx eslint src`

## Directory management

- **./**
  - Store configuration files such as `package.json`, `tsconfig.json`, `.eslintrc.cjs`, `nodemon.json`, and `.gitignore`.
- **src/**: Where the TypeScript source code lives
  - `index.ts` is the entry point to the application
    - It is responsible for starting the server
    - Typically where you would import and use the main functions from other modules
  - *api/* or *routes/*: A directory for organising API routes
    - Each route may have its own `.ts` file, named according to its function, such as `userRoutes.ts`, `productRoutes.ts`, etc
  - *controllers/*: A directory for controller files, containing the logic for handling requests and forming responses
  - *services/*: A directory for service files, where you can place business logic or calls to the OpenAI API
    - For example, a `textGenerationService.ts` for handling text generation with the OpenAI API
  - *models/* or *entities/*: If using a database, this directory will contain files that define the shape of your data, such as `userModel.ts`
  - *utils/* or *helpers/*: For utility or helper functions that are used across multiple modules in your application
    - `helper.ts` may contain reusable functions for error handling, formatting data, etc
  - *tests/* is for test files; if using a testing framework such as Jest, each module in your application might have a corresponding test file here
- **dist/**
  - Where TypeScript (tsc) outputs the compiled JavaScript files
  - Usually included in `.gitignore` as these files are generated and not manually maintained by developers
- **.env**
  - Used for keeping configuration values that are likely to change between deployment environments
    - Such as API keys, database credentials, etc.

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
