# tfip-miniproject

**budgetr** is a small budget planner application inspired by YNAB. It was coded for me to learn key concepts related to site reliability engineering such as containerisation and continuous integration/continuous deployment.

**budgetr** is a full stack application with an Angular frontend and a Spring Boot backend. The backend is secured with Spring Security using JWT. The application is containerised and deployed onto a Google Kubernetes Engine (GKE) cluster. The frontend also lightly uses Angular Material.

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
- 

## Github Actions for CI/CD

- This project uses GitHub Actions in order to trigger an automated build/deploy workflow upon a push to the main branch.
- The workflow sets up Google Cloud CLI as well as user credentials, before building the Docker images, pushing them to Google Cloud Registry and then deploying these images to GKE.
- There is also a side workflow for continuous testing of tests written in the Spring Boot backend.
