# Requirements

- Current project: Budget planning application

## Best practices

### Overall

- Follow style guides
- Error handing and user feedback
  - Always provide user-friendly error messages
  - Provide feedback for every user action
- Using version control
  - Have meaningful commit messages, small commits, feature branches
- Documentation
  - Document your code, APIs, architecture, and setup instructions
- Performance
  - Consider the performance of your application
  - Use caching, database indexing, minification of your CSS and JS, etc.
- Design before developing
  - Prioritize a clear separation of concerns in the architecture of your application
  - Plan your frontend and backend services, along with the interactions and data contracts between them
  - In the SRE context, a well-structured application is easier to monitor, maintain, and debug, which is essential for the reliability of the system.
- Modular development
  - Bundle related functionality into modules

### Angular

- Component-driven design
  - Develop your application using a component-based architecture
  - Promotes reusability and better separation of concerns
- Use Angular CLI
  - Speeds up development and follows best practices in creating components, services, modules, etc
- Lazy loading
  - Implement lazy loading of modules to improve initial loading time of the application
- Asynchronous handling
  - Make use of RxJS observables to handle asynchronous operations and events
- Automated testing
  - Use testing frameworks like Jasmine and Karma for unit testing, and Protractor for end-to-end testing

### Spring Boot

- Layered architecture
  - Follow a layered architecture pattern (Controllers, Services, DAO, DTO)
  - Separates concerns and makes the application more maintainable
- Use DTOs
  - Use Data Transfer Objects (DTOs) for comunication between layers and systems
- Exception handling
  - Implement a global exception handling mechanism
- Validation
  - Implement input validation and use custom exceptions to handle invalid inputs
- Automated testing
  - Write unit tests and integration tests
  - Spring Boot has good integration with JUnit and Mockito
- Security
  - Secure application using Spring Security and JWT

### MySQL/Database

- Database normalisation
  - Important for removing redundancy and improving data integrity
- Indexes
  - Use indexes for faster data retrieval
- Transactions
  - Use transactions where a failure could result in inconsistent data
- Backup
  - Regularly backup your data and test your backups
- Security
  - Don't store plaintext passwords, use encryption, restrict access, and follow least privilege principle

## Mandatory Requirements

### Angular/Frontend

- Use reactive forms
  - Forms for user signup/login, adding new budget entries, updating profile details
- Use GET, POST, PUT, DELETE to communicate between frontend and backend
  - GET the budget entries for display
  - POST a new budget entry
  - PUT to update an edited entry
  - DELETE an entry
- Single Page Application (client-side routing) with a minimum of 4 views
  - Possible views: Dashboard (summary of the budget), details (breakdown of budget entries), profile (user details and settings), AddEntry (form for adding new budget entries)
- Abstract common functionalities into services
  - Abstract logic which is not component-specific
  - HTTP requests, data manipulation, business logic
  - `BudgetService` to handle communication with backend related to budget entries
    - Methods for CRUD to entries
- Include an application manifest
  - Name of the app, icons to be used, start URL, how the app should be displayed when launched

### Spring Boot/Backend

- Use POST to handle either `x-www-form-urlencoded` and/or JSON and/or multipart payload
  - Accept user input e.g. creating a budget, adding a transaction, registering a new user, via HTTP POST
  - Accept JSON payloads
- Making HTTP request to external RESTful API
  - Exchange Rate API (<https://exchangeratesapi.io/>) - convert between different currencies
  - Plaid (<https://plaid.com/>) - financial data API to link users' bank accounts to your application and fetch transaction data
  - SendGrid Email API (<https://sendgrid.com/>) - send transaction notifications or password reset emails to users
  - Easy way out: use a weather API to just fetch weather info and display it on the dashboard
- Parameterised routes
  - e.g. `/users/{userId}`, `/budgets/{budgetId}`
- Query string
  - e.g. `/transactions?userId=1&startDate=2023-01-01&endDate=2023-01-31`
- Must support more than 1 user
  - Implement user registration and authentication
  - Use Spring Security
  - Store user information in MySQL database, authenticate users based on username and password
  - Once authenticated, users should be issued a JWT (JSON Web Token) for authorising subsequent requests

### Database

- Must use MySQL
  - Store structured data e.g. users, budgets, category groups, categories, expenses
  - Keep foreign keys to create relationships between your tables and enforce data integrity
- Modelling data relationships: 1 to 1, 1 to many
  - Users to budgets: 1 to 1
  - The rest: 1 to many
- Demonstrate data integrity and consistency when updating multiple tables
  - Use transactions
  - For example, when updating a `Budget` and related `CategoryGroups` and an error occurred, then changes should be rolled back to ensure data consistency
- Must use another database type e.g. key/value, blob, graph, document
  - Redis/MongoDB can be used for caching/storing session data, temporary data, user preferences, or even the entire user interface state for faster load times
  - In the case of your Angular application, you could serialise the entire state of the application (including all the `Budgets`, `CategoryGroups`, `Categories`, `Expenses`) and store it as a document in MongoDB
    - Makes the application faster and more responsive, as you can load this state directly when the user logs in, instead of making multiple requests to your MySQL database

### Deployment

- The application that you have developed must be publicly accessible. You can deploy your application either as 2 separate deployments, frontend and backend or as a single deployment where the frontend is served from the backend. Applications can be deployed to Railway or any equivalent cloud PaaS platform like Heroku, AppEngine, etc. If Angular is deployed separately, they can be deployed to static web hosting sites or JAM platforms like Vercel, Cloudflare or serving it from your hosted web server. Note: you cannot use GitHub pages for hosting your application.
- All databases must be deployed to the 'cloud'. They can be deployed as VMs in public cloud or using managed database services

## Optional Requirements

### SRE-related

- Containerize your application and deploy into a Kubernetes cluster (10pts)
- Use GitHub Actions for continuous build and continuous deployment to automatically build and deploy your application (6pts)
- Use Spring Boot security with JWT to authenticate and authorize Angular request (5pts)
