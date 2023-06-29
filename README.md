# tfip-miniproject

**budgetr** is a small budget planner application inspired by YNAB. It was coded for me to learn key concepts related to site reliability engineering such as containerisation, Kubernetes and continuous integration/continuous deployment.

**budgetr** is a full stack application with an Angular frontend and a Spring Boot backend. The frontend lightly uses Angular Material. The backend is secured with Spring Security using JWT. The application is containerised and deployed onto a Google Kubernetes Engine (GKE) cluster. And finally, the whole project is built and deployed with an automated CI/CD workflow through GitHub Actions.

Within the project deadline, I didn't manage to fully deploy onto the Kubernetes cluster. Therefore, this project is also deployed onto Railway and Vercel; this was done after the project deadline.

As of the project deadline, the deployment was not ready, but the issue has been resolved since. If you want to have a look, here are the links:

- [Kubernetes Ingress endpoint](http://34.102.214.43/)
- [Vercel](https://tfip-miniproject-ykha.vercel.app/)

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
- To review your expenses, click on `expenses`. A default of `10` expenses, with no limit on the range for the dates.
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

- The backend makes use of Spring Data JPA with Hibernate and Spring Data MongoDB to reduce the effort of implementing simple CRUD methods, as well as creating custom queries.
- The relationships between entities have been mapped in the classes themselves, as such:
  - One [user](sb-backend\src\main\java\ibf2022\tfipminiproject\sqlentities\User.java) to one [budget]([label](sb-backend/src/main/java/ibf2022/tfipminiproject/sqlentities/Budget.java))
  - One budget to many categories
  - One category to many expenses
  - One expense to many comments
- Operations done on a parent entity are cascaded down to its child entities.
  - For example, since a budget should not exist without a user (and a child cannot exist without its parent), deleting a budget's parent user will also result in the deletion of that budget, and its child categories (and so on).
- All entities are stored in a MySQL database, except for `Comment` which is stored in MongoDB. Both databases are hosted on Railway.

### DTOs

- Data Transfer Objects (DTOs) are used to separate the persistence layer from the API layer.
- 
- `MapStruct` is used to 

## Frontend

- The frontend uses Angular Material.

### Routing

- The meat of the frontend is barricaded by the frontpage, which is a signup/login page.
- A user's account is specified in the endpoint by their `UUID`, which is automatically generated upon user registration.
- Other resources, such as categories and expenses, are specified by their `UUID` in endpoints as well, with an effort to preserve a hierarchy (budget -> category -> expense). Feel free to browse [app-routing.module.ts](ng-frontend\src\app\app-routing.module.ts).

### Services

### Requests

## Spring Security

- Upon authenticating through the frontend, the backend returns a JSON web token (JWT).
  - The JWT contains information about the user, as well as a signature, so that the backend can verify that the sender of the JWT is indeed the correct user.
- Some endpoints are not protected, such as authentication endpoints and health check endpoints (these are for GKE).
- Since the backend endpoints hold sensitive information, they are protected.
- The JWT is stored in your local storage through Angular, and then used in the `Authorization` header when making requests to protected endpoints in the backend.
- This token is also used on the frontend to verify that you are permitted to access its endpoints (e.g. the userId in the endpoint is the same as your userId).

### Making API calls

- The `UUID`s you see correspond to an entity's UUID.

## Containerisation and Kubernetes

- Both the frontend and backend are containerised with Docker, with the instructions in the `Dockerfile` of each project directory.
- Each `Dockerfile` makes use of caching of layers for faster builds.
- The manifests for the Kubernetes cluster are in the `k8s` folder.
- The `Deployment` and `Service` components of each portion of the app are defined in a template `.yaml` file.
- A `Secret` is used for credentials; it was applied before applying the `.yaml` files.
- Both services are internal services.
  - An `Ingress` is used to expose the services. It can be accessed [here](http://34.102.214.43/), but the Spring Boot backend is failing health checks.
    - Some time after the project deadline, a fix has been made and now the app can be visited at the IP address given above.

## Github Actions for CI/CD

- This project uses GitHub Actions in order to trigger automated build/deploy workflow upon pushes to the main branch.
- The main workflow sets up Google Cloud CLI as well as user credentials, before building the Docker images, pushing them to Google Cloud Registry and then deploying the app to GKE by applying K8s manifest files.
- There is also a side workflow for continuous testing of tests written in the Spring Boot backend.
- After the project deadline, a workflow was set up for deploying the Spring Boot backend to Railway.
