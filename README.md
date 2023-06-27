# tfip-miniproject

**budgetr** is a small budget planner application inspired by YNAB. It was coded for me to learn key concepts related to site reliability engineering such as containerisation and continuous integration/continuous deployment.

**budgetr** is a full stack application with an Angular frontend and a Spring Boot backend. The backend is secured with Spring Security using JWT. The application is containerised and deployed onto a Google Kubernetes Engine (GKE) cluster. The frontend also lightly uses Angular Material.

## Spring Security

- Upon authenticating through the frontend, the backend returns a JSON web token (JWT).
  - The JWT
- This token is stored in your local storage and then used in the `Authorization` header when making requests to protected endpoints in the backend.

## Containerisation and Kubernetes

## Github Actions for CI/CD

- This project uses GitHub Actions in order to trigger an automated build/deploy workflow upon a push to the main branch.
- Using the Google Cloud CLI, 
