# tfip-miniproject

**budgetr** is a small budget planner application inspired by YNAB. It was coded for me to learn key concepts related to site reliability engineering such as containerisation, Kubernetes and continuous integration/continuous deployment.

**budgetr** is a full stack application with an Angular frontend and a Spring Boot backend. The backend is secured with Spring Security using JWT. The application is containerised and deployed onto a Google Kubernetes Engine (GKE) cluster. The frontend also lightly uses Angular Material.

Within the project deadline, I didn't manage to fully deploy onto the Kubernetes cluster. Therefore, this project is also deployed onto Railway and Vercel; this was done after the project deadline.

As of the project deadline, the deployment was not ready, but the issue has been resolved since. If you want to have a look, here are the links:

- Kubernetes Ingress endpoint: [http://34.102.214.43/]
- Vercel: [https://tfip-miniproject-ykha.vercel.app/]

## Spring Security

- Upon authenticating through the frontend, the backend returns a JSON web token (JWT).
  - The JWT contains information about the user, as well as a signature, so that the backend can verify that the sender of the JWT is indeed the correct user.
- Some endpoints are not protected, such as authentication endpoints and health check endpoints (these are for GKE).
- Since the backend endpoints hold sensitive information, they are protected.
- The JWT is stored in your local storage through Angular, and then used in the `Authorization` header when making requests to protected endpoints in the backend.
- This token is also used on the frontend to verify that you are permitted to access its endpoints (e.g. the userId in the endpoint is the same as your userId).

## Containerisation and Kubernetes

- Both the frontend and backend are containerised with Docker, with the instructions in the `Dockerfile` of each project directory.
- Each `Dockerfile` makes use of caching of layers for faster builds.
- The manifests for the Kubernetes cluster are in the `k8s` folder.
- The `Deployment` and `Service` components of each portion of the app are defined in a template `.yaml` file.
- A `Secret` is used for credentials; it was applied before applying the `.yaml` files.
- Both services are internal services.
  - An `Ingress` is used to expose the services. It can be accessed at [http://34.102.214.43/], but the Spring Boot backend is failing health checks.
    - Some time after the project deadline, a fix has been made and now the app can be visited at the IP address given above.

## Github Actions for CI/CD

- This project uses GitHub Actions in order to trigger automated build/deploy workflow upon pushes to the main branch.
- The main workflow sets up Google Cloud CLI as well as user credentials, before building the Docker images, pushing them to Google Cloud Registry and then deploying the app to GKE by applying K8s manifest files.
- There is also a side workflow for continuous testing of tests written in the Spring Boot backend.
- After the project deadline, a workflow was set up for deploying the Spring Boot backend to Railway.
