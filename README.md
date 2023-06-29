# tfip-miniproject

**budgetr** is a small budget planner application inspired by YNAB. It was coded for me to learn key concepts related to site reliability engineering such as containerisation, Kubernetes and continuous integration/continuous deployment.

**budgetr** is a full stack application with an Angular frontend and a Spring Boot backend. The frontend lightly uses Angular Material. The backend is secured with Spring Security using JWT. The application is containerised and deployed onto a Google Kubernetes Engine (GKE) cluster on Google Cloud Platform (GCP). And finally, the whole project is built and deployed with an automated CI/CD workflow through GitHub Actions.

Within the project deadline, I didn't manage to fully deploy onto the Kubernetes cluster. Therefore, this project is also deployed onto Railway and Vercel; this was done after the project deadline.

As of the project deadline, the deployment was not ready, but the issue has been resolved since. If you want to have a look, here are the links:

- **GKE cluster on GCP**
  - [Frontend endpoint](http://34.102.214.43/)
  - [Backend endpoint](http://34.102.214.43/api) (requires authentication)
- **Vercel and Railway Deployment**
  - [Frontend on Vercel](https://tfip-miniproject-ykha.vercel.app/)
  - [Backend on Railway](https://tart-throat-production.up.railway.app/) (requires authentication)

## budgetr

### Registering/logging in

- Create a user to start, or log in with the following credentials:

```text
email: test6@mail.com
password: password
```

- Since data transfer is not secure, please do not use any of your existing credentials.

### Using the app

- Just like in YNAB, you give every dollar a job. Start by creating a budget with a name and a money pool.
- Now, you have to assign every dollar to a category. Start by creating a category, and how much money you wish to assign to it.
  - The amount you specify for each category will be deducted from the budget's money pool.
- To spend from a category, click on `expenses` and create an expense under the category.
  - The amount you put in the expense will be deducted from its category.
- Once you have made an expense, feel free to add a comment to it by clicking on the expense in the list of expenses.
- To review your categories, click on `budget`. All categories are displayed.
- To review your expenses, click on `expenses`. This page shows a default of `10` expenses, with no limit on the range for the dates.
  - Feel free to specify how many expenses you'd like in a single page, as well as the range for the date.
  - You can also browse through pages using the arrows at the bottom of the table.
- The goal is to assign each dollar in your budget's money pool such that the amount left is zero.
  - When you have done that, then you have given every dollar a job.
  - You now know the purpose of each dollar and how much from each category you can spare.
- You may have overassigned money away from your budget or your categories (meaning the amount is negative), and the app will flash a red warning as an indication.
  - The app does not support updating a category or expense, so you will have to delete it and re-enter them.

### Logging out

- When inside the app, simply click on `logout`. You will need to login again for subsequent uses.

### What else?

- If you like the basic idea of this app, please try out YNAB. It is way more comprehensive, and hardcore budgeters swear by it.
- You can try YNAB free for 34 days. Start [here](https://www.ynab.com/).

## Backend

- The backend is a Spring Boot application built with Maven.

### Persistence

- The backend makes use of Spring Data JPA with Hibernate, and Spring Data MongoDB, to automatically generate the implementation of simple CRUD methods, as well as custom queries.
- The relationships between entities have been mapped in the classes themselves, as such:
  - One [user](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/User.java) to one budget
  - One [budget](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/Budget.java) to many categories
  - One [category](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/Category.java) to many expenses
  - One [expense](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/Expense.java) to many [comments](sb-backend/src/main/java/ibf2022/tfipminiproject/mongoentities/Comment.java)
- Operations done on a parent entity are cascaded down to its child entities.
  - For example, since a budget should not exist without a user (and a child cannot exist without its parent), deleting a budget's parent user will also result in the deletion of that budget, and its child categories (and so on).
- All entities are stored in a MySQL database, except for `Comment` which is stored in MongoDB. Both databases are hosted on Railway.

### DTOs

- Data Transfer Objects (DTOs) are used to separate the persistence layer from the API layer.
  - They are used to selectively expose the data needed by the frontend without having to transfer the entire entity.
- `MapStruct` is used to simplify the implementation of mapping between a DTO and an entity.
  - For example, check out a [budget](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/Budget.java) and its [DTO](sb-backend/src/main/java/ibf2022/tfipminiproject/dtos/BudgetDTO.java), and the [mapper](sb-backend/src/main/java/ibf2022/tfipminiproject/mappers/BudgetMapper.java).
  - Based on the annotations and field names given, the boilerplate implementation of each mapper is automatically generated.

### Exceptions

- A [GlobalExceptionHandler](sb-backend/src/main/java/ibf2022/tfipminiproject/exceptions/GlobalExceptionHandler.java) handles certain exceptions, custom or otherwise, and returns the appropriate HTTP response.

## Frontend

- The frontend uses Angular Material.

### Routing

- The bulk of the frontend is barricaded by the frontpage, which is a signup/login page.
- A user's account is specified in the endpoint by their `UUID`, which is automatically generated upon user registration (when the user's details are persisted to the database).
- Other resources, such as categories and expenses, are specified by their `UUID` in endpoints as well, with an effort to preserve a hierarchy (budget -> category -> expense). Feel free to browse [app-routing.module.ts](ng-frontend/src/app/app-routing.module.ts).
- A route guard function [canActivate](ng-frontend/src/app/auth/auth-guard.service.ts) checks if a user can visit a certain route by checking if they are authenticated (see the *Spring Security* section).
  - All endpoints beyond the frontpage are guarded, since they are used to access a user's details.

### Services

- Beyond regular services for making HTTP requests, there are also services for handling your authentication token (Spring Security), passing this token into backend requests, and errors.
  - The [token service](ng-frontend/src/app/services/token.service.ts) stores authentication tokens generated by the backend, as well as checks its validity (in terms of expiry time).
  - The [HTTP interceptor](ng-frontend/src/app/auth/auth.interceptor.ts) checks for the presence of your token as proof of authentication, then passes it into the `Authorization` header of requests sent to protected endpoints of the backend.
  - The [error service](ng-frontend/src/app/services/error.service.ts) catches various error statuses from backend responses and returns an appropriate message describing the error which can be displayed on the frontend.

## Spring Security

- When a user successfully authenticates, the backend returns a JSON web token (JWT).
  - The JWT contains information about the user, as well as a signature, so that the backend can verify that the sender of the JWT is indeed the correct user.
- Some endpoints are not protected (see [security configuration](sb-backend/src/main/java/ibf2022/tfipminiproject/configs/SecurityConfig.java)), such as [authentication endpoints](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/AuthenticationController.java) and [health check endpoints for GKE](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/HealthController.java).
- Since the backend endpoints hold sensitive information about a user, they are protected.
  - Requests to protected endpoints without authentication will return a `403 FORBIDDEN` status.
- The JWT is stored in your local storage through Angular, which passes it into the `Authorization` header using the `Bearer` scheme when making requests to protected endpoints in the backend.
- This token is also used on the frontend to verify that you are permitted to access its endpoints (e.g. the `userId` specified in the endpoint is the same as your `userId`).

### Making authorised API calls to the backend

- You can make an API call to the backend for registration and authentication. No JWT is required.
- Registration: `/api/v1/auth/register`

```json
// Request
{
    "email": "test@mail.com",
    "password": "password"
}

// Response
{
    "userId": "UUID",
    "token": "example JWT",
    "expiresAt": 1688139771000
}
```

- Authentication: `/api/v1/auth/authenticate`

```json
// Request
{
    "email": "test@mail.com",
    "password": "password"
}

// Response
{
    "userId": "UUID",
    "token": "example JWT",
    "expiresAt": 1688139771000
}
```

- Save this token if you'd like to make API calls to the protected endpoints.
  - Under the `Authorization` header, use the `Bearer` scheme and paste the token inside.
- The `UUIDs` you see in the frontend correspond to an entity's UUID.
- Even while you are authenticated, the backend checks for these for security purposes:
  - The `userId` you've passed into the endpoint matches the `userId` corresponding to your JWT.
  - A resource that you are trying to access must belong to you. These checks are done in [Service](sb-backend/src/main/java/ibf2022/tfipminiproject/services) classes which handle entities.
    - For example, if you have specified your own `userId`, you cannot specify a `Budget` of another user, because that `Budget` does not belong to you.
    - In another example, when trying to save an `Expense` to a `Category`, you cannot specify a `Category` that does not belong to you.
- Using these `UUIDs` as necessary, free to make API calls to the following protected endpoints to test them:
  - ***Do not make `PUT` requests; these are unimplemented and untested.***
  - Refer to the [DTOs](sb-backend/src/main/java/ibf2022/tfipminiproject/dtos) to see what responses to expect from the backend.
  - Refer to the entities to see what fields are required ([MySQL entities](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities); for `Comment`, only `text` is required) from the backend for `POST` requests. The backend expects request bodies in JSON format.
  - [**Budget**](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/BudgetController.java)
    - `GET /api/v1/{userId}/budget` - gets a user's budget
    - `POST /api/v1/{userId}/budget` - create a budget for a user
    - `DELETE /api/v1/{userId}/budget/{budgetId}` - delete the user's specified budget
  - [**Category**](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/CategoryController.java)
    - `GET /api/v1/{userId}/categories/{categoryId}` - gets the user's specified category
    - `GET /api/v1/{userId}/categories` - gets all categories belonging to the user
    - `POST /api/v1/{userId}/{budgetId}/categories` - creates a category under the user's specified budget
    - `DELETE /api/v1/{userId}/categories/{categoryId}` - delete the user's specified category
  - [**Expense**](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/ExpenseController.java)
    - `GET /api/v1/{userId}/expenses/{expenseId}` - gets the user's specified expense
    - `GET /api/v1/{userId}/expenses?page={page}&size={size}&from={from}&to={to}` - retrieves all expenses belonging to the user, with optional pagination parameters `page` and `size` defaulting to `0` and `10` respectively, and an optional date range parameters `from` and `to` in the 'yyyy-MM-dd' format. If `from` and/or `to` are not provided, it will return expenses for all dates.
    - `POST /api/v1/{userId}/{categoryId}/expenses` - creates an expense under the user's specified category
    - `DELETE /api/v1/{userId}/{categoryId}/expenses` - delete the user's specified expense
  - [**Comment**](sb-backend/src/main/java/ibf2022/tfipminiproject/controllers/CommentController.java)
    - `GET /api/v1/{userId}/{expenseId}/comments` - gets all comments for the user's specific expense
    - `POST /api/v1/{userId}/{expenseId}/comments` - creates an comment under the user's specified expense
    - `DELETE /api/v1/{userId}/comments/{commentId}` - deletes the user's specified comment

## Containerisation

- Both the [frontend](ng-frontend/Dockerfile) and [backend](sb-backend/Dockerfile) are containerised with Docker.
- In an attempt at optimisation, each `Dockerfile` makes use of caching of layers, and multi-stage builds, where one stage uses a base image for building, and a different image for running.

## Kubernetes

- The application is deployed onto a [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine) cluster on [Google Cloud Platform](https://cloud.google.com/).
- The manifests for the Kubernetes cluster are in the [k8s folder](k8s).
- The `Deployment` describes the desired state for each application, such as which Docker image to use, number of replicas to run (set to default at `1`), and so on.
- A `Service` allows networking access to instances of the application (or `Pods`) via a stable IP and port, so that this application can talk to other applications.
- The `Deployment` and `Service` components for both the [frontend](k8s/ng-frontend-template.yaml) and [backend](k8s/sb-backend-template.yaml) are defined in a template `.yaml` file.
  - Through an automated workflow, the templates are converted to the actual files to be used during deployment. This is explained later in the `GitHub Actions` section.
- For both deployments, a readiness probe is specified. Kubernetes will make diagnostic `HTTP GET` requests to these specified endpoints and ports to check for a desired response (e.g. `200 OK`.)
  - If a readiness probe fails for a particular instance of a deployment, then that instance is restarted.
- A [Secret](k8s/app-secret-template.yaml), which contains sensitive information such as database credentials and the JWT signing key, is applied.
  - These values are passed into the specification for the Spring Boot backend deployment, which in turn will be passed as environmental variables for its container.
- To expose both the frontend and backend, an [Ingress](k8s/app-ingress.yaml) is used.
  - The `Ingress` manages external access to services within the cluster, such as the services for the frontend and backend.
  - It routes traffic from specified paths [/**](http://34.102.214.43/) to the frontend and [/api/**](http://34.102.214.43/api) to the backend.

## Github Actions for CI/CD

- This project uses GitHub Actions in order to trigger automated workflows upon pushes to the main branch.
- The main workflow [(Build and Deploy to GKE)](https://github.com/echarin/tfip-miniproject/actions/workflows/gke-deploy.yml) sets up Google Cloud CLI as well as user credentials, before building the Docker images, pushing them to Google Cloud Registry and then deploying the app to GKE by applying K8s manifest files.
- There is also a side workflow [(Spring Boot CI)](https://github.com/echarin/tfip-miniproject/actions/workflows/sb-ci.yaml) for automated testing of tests written in the Spring Boot backend.
- After the project deadline, a workflow [(Deploy Spring Boot backend to Railway)](https://github.com/echarin/tfip-miniproject/actions/workflows/railway-deploy.yml) was set up for deploying the Spring Boot backend to Railway.
